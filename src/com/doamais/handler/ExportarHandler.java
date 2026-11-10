package com.doamais.handler;

import com.doamais.exception.DoaMaisException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Exportacao de dados em CSV (requisito de leitura/gravacao em arquivo).
 * Prefixo: "/exportar".
 *
 * Rotas:
 * <ul>
 *   <li>GET /exportar                 pagina com um link de download por tabela
 *                                     (doadores, receptores, doacoes, estoque_hemoes)</li>
 *   <li>GET /exportar?tabela=doadores download do CSV (CsvUtil.exportar para um
 *                                     StringWriter), Content-Type text/csv e header
 *                                     Content-Disposition: attachment; filename="doadores.csv"</li>
 * </ul>
 * Tabela fora da lista permitida -> ValidacaoException.
 *
 * Responsavel: Gabriel.
 */
public class ExportarHandler extends BaseHandler {

    @Override
    protected void get(HttpExchange ex) throws DoaMaisException, IOException {
        // TODO(Gabriel)
        throw new UnsupportedOperationException("TODO(Gabriel): ExportarHandler.get");
    }
}
