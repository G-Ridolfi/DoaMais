package com.doamais.handler;

import com.doamais.dao.DoacaoDAO;
import com.doamais.dao.DoadorDAO;
import com.doamais.dao.ReceptorDAO;
import com.doamais.exception.DoaMaisException;
import com.doamais.model.Doacao;
import com.doamais.model.Doador;
import com.doamais.model.Receptor;
import com.doamais.model.TipoSanguineo;
import com.doamais.service.DoacaoService;
import com.doamais.util.FormUtil;
import com.doamais.util.HtmlUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Telas de Doacoes. Prefixo: "/doacoes".
 *
 * Rotas:
 * <ul>
 *   <li>GET  /doacoes                  historico (DoacaoDAO.listarRecentes) e
 *                                      estatisticas por tipo (DoacaoDAO.contarPorTipo)</li>
 *   <li>GET  /doacoes/nova?receptorId= formulario: doadorId (select com os doadores),
 *                                      receptorId (select com campanhas ativas + opcao
 *                                      "Estoque geral" com valor vazio; pre-selecionado
 *                                      pelo parametro), dataDoacao (padrao hoje), local</li>
 *   <li>POST /doacoes/nova             DoacaoService.registrar(...); sucesso -> redireciona
 *                                      para /campanhas/ver?id= (direcionada) ou /doacoes</li>
 * </ul>
 * Se o doador estiver inapto, a DoadorInaptoException sobe e o BaseHandler
 * mostra a mensagem com os motivos e a data de liberacao.
 *
 * Responsavel: Amon.
 */
public class DoacaoHandler extends BaseHandler {

    private static final String LOCAL_PADRAO = "HEMOES – Vitória";

    private final DoacaoDAO doacaoDAO = new DoacaoDAO();
    private final DoadorDAO doadorDAO = new DoadorDAO();
    private final ReceptorDAO receptorDAO = new ReceptorDAO();
    private final DoacaoService service = new DoacaoService();

    @Override
    protected void get(HttpExchange ex) throws DoaMaisException, IOException {
        String caminho = getCaminho(ex);
        if ("/doacoes".equals(caminho) || "/doacoes/".equals(caminho)) {
            enviarHtml(ex, paginaHistorico());
        } else if ("/doacoes/nova".equals(caminho)) {
            Integer receptorIdUrl = FormUtil.getIntOpcional(FormUtil.parseQuery(ex.getRequestURI()), "receptorId");
            enviarHtml(ex, paginaFormulario(receptorIdUrl));
        } else {
            enviarHtml(ex, 404, HtmlUtil.pagina("Página não encontrada",
                    HtmlUtil.alerta("aviso", "A página que você procura não existe.")));
        }
    }

    @Override
    protected void post(HttpExchange ex, Map<String, String> form) throws DoaMaisException, IOException {
        if (!"/doacoes/nova".equals(getCaminho(ex))) {
            enviarHtml(ex, 404, HtmlUtil.pagina("Página não encontrada",
                    HtmlUtil.alerta("aviso", "A página que você procura não existe.")));
            return;
        }

        // FormUtil converte e valida cada campo (ValidacaoException se algo estiver errado)
        Doacao doacao = service.registrar(
                FormUtil.getInt(form, "doadorId"),
                FormUtil.getIntOpcional(form, "receptorId"),
                FormUtil.getData(form, "dataDoacao", true),
                FormUtil.getTexto(form, "local", false));

        // Post/Redirect/Get: direcionada vai para a campanha; estoque geral volta ao historico
        if (doacao.isDirecionada()) {
            redirecionar(ex, "/campanhas/ver?id=" + doacao.getReceptor().getId());
        } else {
            redirecionar(ex, "/doacoes");
        }
    }

    /** Tela /doacoes: ultimas 20 doacoes e total por tipo sanguineo. */
    private String paginaHistorico() throws DoaMaisException {
        List<Doacao> recentes = doacaoDAO.listarRecentes(20);
        // COLECAO (Map): o EnumMap devolvido por contarPorTipo e percorrido com entrySet()
        Map<TipoSanguineo, Integer> porTipo = doacaoDAO.contarPorTipo();

        StringBuilder html = new StringBuilder();
        html.append("<p><a class=\"botao\" href=\"/doacoes/nova\">🩸 Registrar doação</a></p>\n");

        html.append("<h2>Últimas doações</h2>\n");
        if (recentes.isEmpty()) {
            html.append(HtmlUtil.alerta("info", "Nenhuma doação registrada ainda."));
        } else {
            html.append("<table class=\"tabela\">\n<thead><tr>")
                .append("<th>Data</th><th>Doador</th><th>Destino</th><th>Local</th>")
                .append("</tr></thead>\n<tbody>\n");
            for (Doacao d : recentes) {
                // POLIMORFISMO/ASSOCIACAO: navega Doacao -> Doador / Receptor
                String destino = d.isDirecionada() ? d.getReceptor().getNome() : "Estoque geral";
                html.append("<tr><td>").append(HtmlUtil.formatarData(d.getDataDoacao())).append("</td>")
                    .append("<td>").append(HtmlUtil.escapar(d.getDoador().getNome())).append("</td>")
                    .append("<td>").append(HtmlUtil.escapar(destino)).append("</td>")
                    .append("<td>").append(HtmlUtil.escapar(d.getLocal())).append("</td></tr>\n");
            }
            html.append("</tbody>\n</table>\n");
        }

        html.append("<h2>Doações por tipo sanguíneo</h2>\n")
            .append("<table class=\"tabela\">\n<thead><tr><th>Tipo</th><th>Doações</th></tr></thead>\n<tbody>\n");
        for (Map.Entry<TipoSanguineo, Integer> e : porTipo.entrySet()) {
            html.append("<tr><td>").append(HtmlUtil.escapar(e.getKey().getRotulo())).append("</td>")
                .append("<td>").append(e.getValue()).append("</td></tr>\n");
        }
        html.append("</tbody>\n</table>\n");

        return HtmlUtil.pagina("Doações", html.toString());
    }

    /** Tela /doacoes/nova: formulario de registro. */
    private String paginaFormulario(Integer receptorIdSelecionado) throws DoaMaisException {
        List<Doador> doadores = doadorDAO.listarTodos();
        List<Receptor> campanhas = receptorDAO.listarAtivas();

        StringBuilder html = new StringBuilder();
        html.append("<form method=\"post\" action=\"/doacoes/nova\" class=\"formulario\">\n");

        html.append("  <label for=\"doadorId\">Doador</label>\n")
            .append("  <select name=\"doadorId\" id=\"doadorId\" required>\n");
        for (Doador d : doadores) {
            html.append("    <option value=\"").append(d.getId()).append("\">")
                .append(HtmlUtil.escapar(d.getNome())).append(" (")
                .append(HtmlUtil.escapar(d.getTipoSanguineo().getRotulo())).append(")</option>\n");
        }
        html.append("  </select>\n");

        html.append("  <label for=\"receptorId\">Destino</label>\n")
            .append("  <select name=\"receptorId\" id=\"receptorId\">\n")
            .append("    <option value=\"\">Estoque geral</option>\n");
        for (Receptor r : campanhas) {
            boolean marcado = receptorIdSelecionado != null && receptorIdSelecionado == r.getId();
            html.append("    <option value=\"").append(r.getId()).append("\"")
                .append(marcado ? " selected" : "").append(">")
                .append(HtmlUtil.escapar(r.getNome())).append(" – ")
                .append(HtmlUtil.escapar(r.getTipoSanguineo().getRotulo())).append("</option>\n");
        }
        html.append("  </select>\n");

        html.append("  <label for=\"dataDoacao\">Data da doação</label>\n")
            .append("  <input type=\"date\" name=\"dataDoacao\" id=\"dataDoacao\" value=\"")
            .append(LocalDate.now()).append("\" required>\n");

        html.append("  <label for=\"local\">Local</label>\n")
            .append("  <input type=\"text\" name=\"local\" id=\"local\" value=\"")
            .append(HtmlUtil.escapar(LOCAL_PADRAO)).append("\">\n");

        html.append("  <button type=\"submit\" class=\"botao\">Registrar doação</button>\n")
            .append("</form>\n");

        return HtmlUtil.pagina("Registrar doação", html.toString());
    }
}