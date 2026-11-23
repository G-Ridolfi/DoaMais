package com.doamais.handler;

import com.doamais.dao.EstoqueDAO;
import com.doamais.dao.ReceptorDAO;
import com.doamais.exception.DoaMaisException;
import com.doamais.model.NivelEstoque;
import com.doamais.model.Receptor;
import com.doamais.model.TipoSanguineo;
import com.doamais.util.HtmlUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Pagina inicial. Rota: "/" (qualquer outro caminho nao mapeado -> 404).
 *
 * Conteudo:
 * <ol>
 *   <li>TERMOMETRO DO HEMOES no topo: 8 badges (EstoqueDAO.carregarTermometro()
 *       + HtmlUtil.badgeEstoque), destacando os tipos em nivel CRITICO;</li>
 *   <li>FEED DE CAMPANHAS ATIVAS: ReceptorDAO.listarAtivas() + HtmlUtil.cardCampanha;</li>
 *   <li>atalhos para "Fazer pré-triagem" (/aptidao) e "Criar campanha" (/campanhas/nova).</li>
 * </ol>
 *
 * Responsavel: Maria Vitoria.
 */
// #11 Heranca + Polimorfismo: herda da BaseHandler e sobrescreve apenas o get()
public class HomeHandler extends BaseHandler {

    /**
     * POLIMORFISMO: sobrescreve o get() abstrato da BaseHandler; o handle()
     * herdado chama esta versao quando a requisicao e um GET em "/".
     */
    @Override
    protected void get(HttpExchange ex) throws DoaMaisException, IOException {
        String caminho = getCaminho(ex);
        // #12 Garante 404 para enderecos desconhecidos em vez de abrir a Home errada
        if (!"/".equals(caminho)) {
            enviarHtml(ex, 404, HtmlUtil.pagina("Página não encontrada",
                    HtmlUtil.alerta("erro", "Endereço não encontrado: " + caminho)
                            + "<p><a class=\"botao\" href=\"/\">Ir para o início</a></p>"));
            return;
        }

        String html = montarTermometro() + montarAtalhos() + montarFeed();
        enviarHtml(ex, HtmlUtil.pagina("Doe sangue, salve vidas", html));
    }

    /** Termometro do HEMOES: 8 badges + frase de alerta com os tipos criticos. */
    private String montarTermometro() throws DoaMaisException {
        // #13 Colecao + Leitura do banco: EnumMap por usar chave enum (leve e na ordem dos tipos)
        EnumMap<TipoSanguineo, NivelEstoque> estoque = new EstoqueDAO().carregarTermometro();

        StringBuilder badges = new StringBuilder();
        // #14 Colecao: ArrayList dinamico para reunir os tipos em nivel critico no alerta urgente
        List<String> criticos = new ArrayList<>();
        estoque.forEach((tipo, nivel) -> {
            badges.append(HtmlUtil.badgeEstoque(tipo, nivel)).append('\n');
            if (nivel == NivelEstoque.CRITICO) {
                criticos.add(tipo.getRotulo());
            }
        });

        StringBuilder html = new StringBuilder();
        html.append("<section class=\"secao-termometro\">\n")
            .append("<h2>Estoque do HEMOES hoje</h2>\n");
        if (!criticos.isEmpty()) {
            html.append(HtmlUtil.alerta("aviso", "Urgente: " + juntar(criticos)
                    + " em nível crítico. Sua doação faz diferença!")).append('\n');
        }
        html.append("<div class=\"termometro\">\n").append(badges).append("</div>\n")
            .append("</section>\n");
        return html.toString();
    }

    /** Botoes de atalho para as acoes principais. */
    private String montarAtalhos() {
        return "<section class=\"atalhos\">\n"
                + "  <a class=\"botao botao-grande\" href=\"/aptidao\">Posso doar? Faça a pré-triagem</a>\n"
                + "  <a class=\"botao botao-grande botao-secundario\" href=\"/campanhas/nova\">Criar campanha</a>\n"
                + "</section>\n";
    }

    /** Feed com os cards das campanhas ativas (mais urgentes primeiro). */
    private String montarFeed() throws DoaMaisException {
        // #15 Colecao + Leitura do banco: lista campanhas ativas do SQLite para gerar os cards
        List<Receptor> campanhas = new ReceptorDAO().listarAtivas();

        StringBuilder html = new StringBuilder();
        html.append("<section class=\"secao-feed\">\n<h2>Campanhas ativas</h2>\n");
        if (campanhas.isEmpty()) {
            html.append(HtmlUtil.alerta("info", "Nenhuma campanha ativa no momento.")).append('\n');
        } else {
            html.append("<div class=\"grade-cards\">\n");
            for (Receptor r : campanhas) {
                html.append(HtmlUtil.cardCampanha(r)).append('\n');
            }
            html.append("</div>\n");
        }
        html.append("</section>\n");
        return html.toString();
    }

    /** ["O-", "B-"] -> "O- e B-"; ["A-", "O-", "B-"] -> "A-, O- e B-". */
    private String juntar(List<String> itens) {
        if (itens.size() == 1) {
            return itens.get(0);
        }
        return String.join(", ", itens.subList(0, itens.size() - 1)) + " e " + itens.get(itens.size() - 1);
    }
}
