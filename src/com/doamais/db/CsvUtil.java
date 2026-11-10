package com.doamais.db;

import com.doamais.exception.DAOException;
import com.doamais.exception.DoaMaisException;
import com.doamais.exception.ValidacaoException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Leitura e gravacao de dados em arquivos CSV (requisito do edital de LPOO).
 *
 * Formato: separador ";" (abre direto no Excel em portugues), UTF-8,
 * primeira linha = nomes das colunas do banco, campo vazio = NULL,
 * campos com ";" ou aspas ficam entre aspas duplas.
 *
 * Tabelas permitidas: doadores, receptores, doacoes, estoque_hemoes
 * (qualquer outra -&gt; ValidacaoException; nunca concatenar nome de tabela
 * vindo do usuario sem validar).
 *
 * Dados da demonstracao: pasta dados/seed/ (doadores.csv, receptores.csv,
 * doacoes.csv), carregados por carregarDadosIniciais().
 *
 * Responsavel: Gabriel.
 */
public final class CsvUtil {

    /** Separador de campos (padrao do Excel em portugues). */
    private static final char SEPARADOR = ';';

    /** Pasta com os CSVs da demonstracao. */
    private static final String PASTA_SEED = "dados/seed";

    /**
     * COLECAO (Set): unicas tabelas que podem ser exportadas/importadas.
     * Como o nome da tabela entra no texto do SQL, ele so e aceito se estiver
     * nesta lista (protecao contra SQL Injection).
     */
    private static final Set<String> TABELAS_PERMITIDAS =
            Set.of("doadores", "receptores", "doacoes", "estoque_hemoes");

    private CsvUtil() {
    }

    /** Escreve a tabela inteira em CSV no Writer (usado pelo ExportarHandler). */
    public static void exportar(String tabela, Writer destino) throws DoaMaisException {
        validarTabela(tabela);
        // tabela ja validada na lista, por isso pode ser concatenada
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM " + tabela + " ORDER BY 1");
             ResultSet rs = ps.executeQuery()) {

            // Cabecalho: nomes das colunas obtidos dos metadados do ResultSet
            ResultSetMetaData meta = rs.getMetaData();
            int colunas = meta.getColumnCount();
            List<String> campos = new ArrayList<>();
            for (int i = 1; i <= colunas; i++) {
                campos.add(meta.getColumnName(i));
            }
            escreverLinha(destino, campos);

            // Uma linha do CSV por registro do banco
            while (rs.next()) {
                campos.clear();
                for (int i = 1; i <= colunas; i++) {
                    campos.add(rs.getString(i)); // null vira campo vazio
                }
                escreverLinha(destino, campos);
            }
            destino.flush();
        } catch (SQLException e) {
            throw new DAOException("Erro ao ler a tabela " + tabela + " para exportar", e);
        } catch (IOException e) {
            throw new DAOException("Erro ao gravar o CSV da tabela " + tabela, e);
        }
    }

    /** Escreve a tabela inteira em um arquivo .csv. */
    public static void exportar(String tabela, File arquivo) throws DoaMaisException {
        File pasta = arquivo.getAbsoluteFile().getParentFile();
        if (pasta != null && !pasta.exists() && !pasta.mkdirs()) {
            throw new DAOException("Nao foi possivel criar a pasta " + pasta);
        }
        // GRAVACAO EM ARQUIVO: BufferedWriter UTF-8, fechado pelo try-with-resources
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(arquivo), StandardCharsets.UTF_8))) {
            exportar(tabela, out);
        } catch (IOException e) {
            throw new DAOException("Erro ao gravar o arquivo " + arquivo, e);
        }
    }

    /**
     * Le o CSV linha a linha e insere na tabela (colunas pelo cabecalho).
     * Linha mal formatada -&gt; ValidacaoException informando o numero da linha.
     *
     * @return quantidade de linhas importadas
     */
    public static int importar(String tabela, File arquivo) throws DoaMaisException {
        validarTabela(tabela);
        String nomeArquivo = arquivo.getName();

        // LEITURA DE ARQUIVO: BufferedReader UTF-8, fechado pelo try-with-resources
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                new FileInputStream(arquivo), StandardCharsets.UTF_8));
             Connection c = Database.getConnection()) {

            // 1) Cabecalho = nomes das colunas
            String cabecalho = in.readLine();
            if (cabecalho == null || cabecalho.isBlank()) {
                throw new ValidacaoException("Arquivo " + nomeArquivo + " vazio ou sem cabecalho");
            }
            if (cabecalho.startsWith("\uFEFF")) {
                cabecalho = cabecalho.substring(1); // ignora o BOM gerado pelo Excel
            }
            List<String> colunas = dividirLinha(cabecalho);
            for (String coluna : colunas) {
                if (!coluna.matches("[A-Za-z0-9_]+")) {
                    throw new ValidacaoException("Linha 1 do arquivo " + nomeArquivo
                            + ": nome de coluna invalido \"" + coluna + "\"");
                }
            }

            // 2) INSERT com um "?" por coluna (valores nunca concatenados)
            String sql = "INSERT INTO " + tabela + " (" + String.join(", ", colunas) + ") VALUES ("
                    + String.join(", ", Collections.nCopies(colunas.size(), "?")) + ")";

            // 3) TRANSACAO: ou importa o arquivo inteiro, ou nada
            c.setAutoCommit(false);
            int importadas = 0;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                int numeroLinha = 1;
                String linha;
                while ((linha = in.readLine()) != null) {
                    numeroLinha++;
                    int linhaInicial = numeroLinha;
                    // campo entre aspas com quebra de linha: junta as linhas seguintes
                    while (contarAspas(linha) % 2 != 0) {
                        String continuacao = in.readLine();
                        if (continuacao == null) {
                            throw new ValidacaoException("Linha " + linhaInicial + " do arquivo "
                                    + nomeArquivo + ": aspas abertas e nao fechadas");
                        }
                        numeroLinha++;
                        linha = linha + "\n" + continuacao;
                    }
                    if (linha.isBlank()) {
                        continue; // ignora linhas em branco (ex: final do arquivo)
                    }
                    List<String> valores = dividirLinha(linha);
                    if (valores.size() != colunas.size()) {
                        throw new ValidacaoException("Linha " + linhaInicial + " do arquivo "
                                + nomeArquivo + ": esperados " + colunas.size()
                                + " campos, encontrados " + valores.size());
                    }
                    for (int i = 0; i < valores.size(); i++) {
                        String valor = valores.get(i);
                        if (valor.isEmpty()) {
                            ps.setNull(i + 1, Types.NULL); // campo vazio = NULL
                        } else {
                            ps.setString(i + 1, valor);
                        }
                    }
                    ps.addBatch();
                    importadas++;
                }
                ps.executeBatch();
                c.commit();
            } catch (SQLException | ValidacaoException e) {
                c.rollback(); // desfaz tudo que foi inserido deste arquivo
                throw e;
            }
            return importadas;

        } catch (SQLException e) {
            throw new DAOException("Erro ao importar o arquivo " + nomeArquivo
                    + " na tabela " + tabela + ": " + e.getMessage(), e);
        } catch (IOException e) {
            throw new DAOException("Erro ao ler o arquivo " + nomeArquivo, e);
        }
    }

    /**
     * Se a tabela doadores estiver vazia, importa dados/seed/doadores.csv,
     * receptores.csv e doacoes.csv (nessa ordem, por causa das chaves estrangeiras).
     * Chamado por Database.inicializar().
     */
    public static void carregarDadosIniciais() throws DoaMaisException {
        File pasta = new File(PASTA_SEED);
        if (!pasta.isDirectory()) {
            return; // sem pasta de seed: nada a fazer
        }

        try (Connection c = Database.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM doadores")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return; // banco ja tem dados: nao duplica
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao verificar se o banco esta vazio", e);
        }

        // Ordem importa: doacoes referencia doadores e receptores (FK)
        String[] tabelas = {"doadores", "receptores", "doacoes"};
        for (String tabela : tabelas) {
            File arquivo = new File(pasta, tabela + ".csv");
            if (arquivo.isFile()) {
                int linhas = importar(tabela, arquivo);
                System.out.println("Dados iniciais: " + linhas + " registro(s) em " + tabela);
            }
        }
    }

    // ------------------------------------------------------------------
    // Auxiliares PRIVADOS
    // ------------------------------------------------------------------

    /** Lanca ValidacaoException se a tabela nao estiver na lista permitida. */
    private static void validarTabela(String tabela) throws ValidacaoException {
        if (tabela == null || !TABELAS_PERMITIDAS.contains(tabela)) {
            throw new ValidacaoException("Tabela nao permitida para CSV: " + tabela
                    + ". Use uma de: doadores, receptores, doacoes, estoque_hemoes.");
        }
    }

    /** Escreve os campos separados por ";" e termina a linha. */
    private static void escreverLinha(Writer destino, List<String> campos) throws IOException {
        for (int i = 0; i < campos.size(); i++) {
            if (i > 0) {
                destino.write(SEPARADOR);
            }
            destino.write(escaparCampo(campos.get(i)));
        }
        destino.write("\r\n"); // CRLF: padrao do formato CSV e do Excel
    }

    /**
     * null -&gt; "". Campo com ";", aspas ou quebra de linha fica entre aspas
     * duplas, e cada aspa interna e duplicada.
     */
    private static String escaparCampo(String valor) {
        if (valor == null) {
            return "";
        }
        if (valor.indexOf(SEPARADOR) >= 0 || valor.indexOf('"') >= 0
                || valor.indexOf('\n') >= 0 || valor.indexOf('\r') >= 0) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    /**
     * Divide uma linha CSV nos campos, respeitando aspas:
     * {@code a;"b;c";"d ""e"""} -&gt; [a, b;c, d "e"].
     */
    private static List<String> dividirLinha(String linha) {
        List<String> campos = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        boolean entreAspas = false;

        for (int i = 0; i < linha.length(); i++) {
            char ch = linha.charAt(i);
            if (entreAspas) {
                if (ch == '"') {
                    if (i + 1 < linha.length() && linha.charAt(i + 1) == '"') {
                        atual.append('"'); // aspa duplicada = aspa literal
                        i++;
                    } else {
                        entreAspas = false; // fecha as aspas
                    }
                } else {
                    atual.append(ch);
                }
            } else if (ch == '"') {
                entreAspas = true;
            } else if (ch == SEPARADOR) {
                campos.add(atual.toString());
                atual.setLength(0);
            } else if (ch != '\r') {
                atual.append(ch);
            }
        }
        campos.add(atual.toString());
        return campos;
    }

    /** Conta as aspas da linha (quantidade impar = campo continua na proxima linha). */
    private static int contarAspas(String linha) {
        int total = 0;
        for (int i = 0; i < linha.length(); i++) {
            if (linha.charAt(i) == '"') {
                total++;
            }
        }
        return total;
    }
}
