package com.doamais.dao;

import com.doamais.db.Database;
import com.doamais.exception.DAOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Classe ABSTRATA e GENERICA que todos os DAOs com chave "id" herdam.
 *
 * HERANCA: DoadorDAO, ReceptorDAO e DoacaoDAO estendem BaseDAO&lt;T&gt;.
 * POLIMORFISMO: listarTodos() e buscarPorId() sao escritos uma unica vez
 * aqui e chamam mapear(), que cada DAO filho implementa do seu jeito.
 *
 * COMO USAR (exemplo em um DAO filho):
 * <pre>
 *   public List&lt;Doador&gt; listarPorTipo(TipoSanguineo t) throws DAOException {
 *       return consultar("SELECT * FROM doadores WHERE tipo_sanguineo = ?", t.getRotulo());
 *   }
 *   public void inserir(Doador d) throws DAOException {
 *       int id = inserirRetornandoId("INSERT INTO doadores (nome, ...) VALUES (?, ...)",
 *               d.getNome(), ...);
 *       d.setId(id);
 *   }
 * </pre>
 *
 * PARAMETROS dos helpers: passe valores JA CONVERTIDOS para o formato do banco:
 * LocalDate -> data.toString() (ISO yyyy-MM-dd) | TipoSanguineo -> getRotulo()
 * | Sexo -> getCodigo() | StatusCampanha/NivelEstoque -> name()
 * | null e aceito (vira NULL no banco).
 *
 * Todo acesso usa PreparedStatement + try-with-resources, e qualquer
 * SQLException e convertida em DAOException.
 *
 * Responsavel: Gabriel.
 * CONTRATO: assinaturas usadas por Pedro, Ricardo e Amon. Nao alterar sem avisar o grupo.
 *
 * @param <T> tipo do objeto de modelo (Doador, Receptor, Doacao)
 */
public abstract class BaseDAO<T> {

    // ------------------------------------------------------------------
    // Metodos ABSTRATOS: cada DAO filho implementa
    // ------------------------------------------------------------------

    /** @return nome da tabela no banco, ex: "doadores" */
    protected abstract String getTabela();

    /**
     * Converte a linha ATUAL do ResultSet em um objeto (nao chame rs.next() aqui).
     */
    protected abstract T mapear(ResultSet rs) throws SQLException;

    /** Insere o objeto no banco e preenche o id gerado no proprio objeto. */
    public abstract void inserir(T obj) throws DAOException;

    /** Atualiza todos os campos do objeto no banco (pelo id). */
    public abstract void atualizar(T obj) throws DAOException;

    // ------------------------------------------------------------------
    // Metodos PRONTOS: herdados por todos os DAOs
    // ------------------------------------------------------------------

    /** @return o objeto com o id informado, ou Optional.empty() se nao existir */
    public Optional<T> buscarPorId(int id) throws DAOException {
        // getTabela() vem do proprio codigo (nunca do usuario); o id vai via "?"
        List<T> resultado = consultar("SELECT * FROM " + getTabela() + " WHERE id = ?", id);
        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.get(0));
    }

    /** @return todos os registros da tabela, ordenados por id */
    public List<T> listarTodos() throws DAOException {
        // POLIMORFISMO: escrito uma unica vez, mas cada DAO filho converte as
        // linhas com o SEU mapear() (Doador, Receptor, Doacao...).
        return consultar("SELECT * FROM " + getTabela() + " ORDER BY id");
    }

    /** @return true se algum registro foi excluido */
    public boolean excluir(int id) throws DAOException {
        return executar("DELETE FROM " + getTabela() + " WHERE id = ?", id) > 0;
    }

    // ------------------------------------------------------------------
    // Helpers PROTEGIDOS: para os DAOs filhos escreverem menos JDBC
    // ------------------------------------------------------------------

    /**
     * Executa um SELECT e converte cada linha com mapear().
     *
     * @return lista (possivelmente vazia, nunca null)
     */
    protected List<T> consultar(String sql, Object... parametros) throws DAOException {
        // COLECAO: ArrayList guarda os objetos na ordem em que vieram do banco
        List<T> lista = new ArrayList<>();
        // try-with-resources: Connection, PreparedStatement e ResultSet sao
        // fechados automaticamente, mesmo se ocorrer erro
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            preencherParametros(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // POLIMORFISMO: chama o mapear() do DAO filho concreto
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            // TRATAMENTO DE EXCECAO: SQLException nao "vaza" para outras camadas
            throw new DAOException("Erro ao consultar " + getTabela(), e);
        }
        return lista;
    }

    /**
     * Executa INSERT/UPDATE/DELETE.
     *
     * @return quantidade de linhas afetadas
     */
    protected int executar(String sql, Object... parametros) throws DAOException {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            preencherParametros(ps, parametros);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw converterErroDeGravacao(e);
        }
    }

    /**
     * Executa um INSERT e devolve o id gerado (AUTOINCREMENT).
     */
    protected int inserirRetornandoId(String sql, Object... parametros) throws DAOException {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherParametros(ps, parametros);
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    return chaves.getInt(1);
                }
            }
            throw new DAOException("Nenhum id foi gerado ao inserir em " + getTabela());
        } catch (SQLException e) {
            throw converterErroDeGravacao(e);
        }
    }

    // ------------------------------------------------------------------
    // Auxiliares PRIVADOS
    // ------------------------------------------------------------------

    /**
     * Preenche os "?" do SQL na ordem recebida. Os valores nunca sao
     * concatenados no texto do SQL, o que impede SQL Injection.
     */
    private void preencherParametros(PreparedStatement ps, Object... parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            ps.setObject(i + 1, parametros[i]); // indices JDBC comecam em 1
        }
    }

    /**
     * Converte a SQLException de uma gravacao em DAOException. Violacao de
     * UNIQUE (ex: CPF repetido) ganha uma mensagem amigavel para a tela.
     */
    private DAOException converterErroDeGravacao(SQLException e) {
        String mensagem = e.getMessage();
        if (mensagem != null && mensagem.contains("UNIQUE")) {
            return new DAOException("Registro duplicado em " + getTabela(), e);
        }
        return new DAOException("Erro ao gravar em " + getTabela(), e);
    }
}
