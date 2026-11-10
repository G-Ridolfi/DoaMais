package com.doamais.db;

import com.doamais.exception.DoaMaisException;

import java.io.File;
import java.io.Writer;

/**
 * Leitura e gravacao de dados em arquivos CSV (requisito do edital de LPOO).
 *
 * Formato: separador ";" (abre direto no Excel em portugues), UTF-8,
 * primeira linha = nomes das colunas do banco, campo vazio = NULL,
 * campos com ";" ou aspas ficam entre aspas duplas.
 *
 * Tabelas permitidas: doadores, receptores, doacoes, estoque_hemoes
 * (qualquer outra -> ValidacaoException; nunca concatenar nome de tabela
 * vindo do usuario sem validar).
 *
 * Dados da demonstracao: pasta dados/seed/ (doadores.csv, receptores.csv,
 * doacoes.csv), carregados por carregarDadosIniciais().
 *
 * Responsavel: Gabriel.
 */
public final class CsvUtil {

    private CsvUtil() {
    }

    /** Escreve a tabela inteira em CSV no Writer (usado pelo ExportarHandler). */
    public static void exportar(String tabela, Writer destino) throws DoaMaisException {
        // TODO(Gabriel)
        throw new UnsupportedOperationException("TODO(Gabriel): CsvUtil.exportar(Writer)");
    }

    /** Escreve a tabela inteira em um arquivo .csv. */
    public static void exportar(String tabela, File arquivo) throws DoaMaisException {
        // TODO(Gabriel)
        throw new UnsupportedOperationException("TODO(Gabriel): CsvUtil.exportar(File)");
    }

    /**
     * Le o CSV linha a linha e insere na tabela (colunas pelo cabecalho).
     * Linha mal formatada -> ValidacaoException informando o numero da linha.
     *
     * @return quantidade de linhas importadas
     */
    public static int importar(String tabela, File arquivo) throws DoaMaisException {
        // TODO(Gabriel)
        throw new UnsupportedOperationException("TODO(Gabriel): CsvUtil.importar");
    }

    /**
     * Se a tabela doadores estiver vazia, importa dados/seed/doadores.csv,
     * receptores.csv e doacoes.csv (nessa ordem, por causa das chaves estrangeiras).
     * Chamado por Database.inicializar().
     */
    public static void carregarDadosIniciais() throws DoaMaisException {
        // TODO(Gabriel)
        throw new UnsupportedOperationException("TODO(Gabriel): CsvUtil.carregarDadosIniciais");
    }
}
