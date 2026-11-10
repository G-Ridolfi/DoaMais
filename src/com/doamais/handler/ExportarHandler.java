package com.doamais.handler;

import com.doamais.db.CsvUtil;
import com.doamais.exception.DoaMaisException;
import com.doamais.util.HtmlUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

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
 * Tabela fora da lista permitida -&gt; ValidacaoException.
 *
 * Responsavel: Gabriel.
 */
public class ExportarHandler extends BaseHandler {

    /**
     * COLECAO (Map ordenado): tabela -&gt; descricao exibida na pagina.
     * LinkedHashMap mantem a ordem de insercao dos links.
     */
    private static final Map<String, String> TABELAS = new LinkedHashMap<>();

    static {
        TABELAS.put("doadores", "Doadores cadastrados");
        TABELAS.put("receptores", "Campanhas (receptores)");
        TABELAS.put("doacoes", "Doações registradas");
        TABELAS.put("estoque_hemoes", "Termômetro do estoque do HEMOES");
    }

    /**
     * POLIMORFISMO: sobrescreve o get() da BaseHandler; o handle() herdado
     * chama esta versao quando a rota e /exportar.
     */
    @Override
    protected void get(HttpExchange ex) throws DoaMaisException, IOException {
        String caminho = getCaminho(ex);
        if (!"/exportar".equals(caminho) && !"/exportar/".equals(caminho)) {
            enviarHtml(ex, 404, HtmlUtil.pagina("Página não encontrada",
                    HtmlUtil.alerta("erro", "Endereço não encontrado: " + caminho)));
            return;
        }

        String tabela = getParametro(ex, "tabela");
        if (tabela == null || tabela.isBlank()) {
            enviarHtml(ex, HtmlUtil.pagina("Exportar dados", montarListaDeLinks()));
            return;
        }

        // CsvUtil valida a tabela (ValidacaoException se nao for permitida)
        StringWriter csv = new StringWriter();
        CsvUtil.exportar(tabela, csv);

        // BOM UTF-8 no inicio: faz o Excel reconhecer os acentos corretamente
        byte[] corpo = ("\uFEFF" + csv).getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Disposition",
                "attachment; filename=\"" + tabela + ".csv\"");
        enviarResposta(ex, 200, "text/csv; charset=UTF-8", corpo);
    }

    /** Monta a lista HTML com um link de download por tabela. */
    private String montarListaDeLinks() {
        StringBuilder html = new StringBuilder();
        html.append("<p>Baixe os dados do DoaMais em arquivos CSV (separador \";\"), ")
            .append("que abrem diretamente no Excel.</p>\n<ul class=\"lista-exportar\">\n");
        for (Map.Entry<String, String> item : TABELAS.entrySet()) {
            String tabela = HtmlUtil.escapar(item.getKey());
            html.append("  <li><a href=\"/exportar?tabela=").append(tabela).append("\">")
                .append(HtmlUtil.escapar(item.getValue()))
                .append(" (").append(tabela).append(".csv)</a></li>\n");
        }
        html.append("</ul>\n");
        return html.toString();
    }
}
