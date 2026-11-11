package com.doamais.util;

import com.doamais.model.NivelEstoque;
import com.doamais.model.Receptor;
import com.doamais.model.TipoSanguineo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Gera os pedacos de HTML repetidos no sistema, para todas as telas
 * terem o mesmo visual (identidade vinho do DoaMais).
 *
 * REGRA DE SEGURANCA: todo texto vindo do usuario ou do banco deve passar
 * por escapar() antes de entrar no HTML.
 *
 * Responsavel: Maria Vitoria.
 * CONTRATO: usado por todos os handlers. Nao alterar assinaturas sem avisar.
 */
public final class HtmlUtil {

    /** Formato brasileiro de data usado em todas as telas. */
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Itens do menu principal: {endereco, texto}. */
    private static final String[][] MENU = {
        {"/", "Início"},
        {"/doadores", "Doadores"},
        {"/aptidao", "Pré-triagem"},
        {"/campanhas/nova", "Nova campanha"},
        {"/doacoes", "Doações"}
    };

    private HtmlUtil() {
    }

    /** Troca &amp; &lt; &gt; " ' pelas entidades HTML. null -> "". */
    public static String escapar(String texto) {
        if (texto == null) {
            return "";
        }
        // '&' PRIMEIRO, senao as entidades geradas depois seriam escapadas de novo
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Pagina HTML completa: &lt;head&gt; com /static/style.css e /static/app.js,
     * cabecalho com logo e menu (Inicio /, Doadores /doadores, Pre-triagem /aptidao,
     * Nova campanha /campanhas/nova, Doacoes /doacoes),
     * o conteudo e o rodape.
     *
     * @param titulo       titulo da aba e do &lt;h1&gt; (sera escapado)
     * @param conteudoHtml HTML ja pronto do corpo (NAO e escapado)
     */
    public static String pagina(String titulo, String conteudoHtml) {
        String t = escapar(titulo);
        StringBuilder html = new StringBuilder(2048);
        html.append("<!DOCTYPE html>\n")
            .append("<html lang=\"pt-BR\">\n<head>\n")
            .append("  <meta charset=\"UTF-8\">\n")
            .append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n")
            .append("  <title>").append(t).append(" · DoaMais</title>\n")
            .append("  <link rel=\"stylesheet\" href=\"/static/style.css\">\n")
            .append("  <script src=\"/static/app.js\" defer></script>\n")
            .append("</head>\n<body>\n")
            .append("<header class=\"topo\">\n  <div class=\"container topo-conteudo\">\n")
            .append("    <a class=\"logo\" href=\"/\"><span class=\"gota\" aria-hidden=\"true\">🩸</span> DoaMais</a>\n")
            .append("    <nav class=\"menu\" aria-label=\"Menu principal\">\n");
        for (String[] item : MENU) {
            html.append("      <a href=\"").append(item[0]).append("\">").append(item[1]).append("</a>\n");
        }
        html.append("    </nav>\n  </div>\n</header>\n")
            .append("<main class=\"container\">\n")
            .append("  <h1>").append(t).append("</h1>\n")
            .append(conteudoHtml == null ? "" : conteudoHtml).append("\n")
            .append("</main>\n")
            .append("<footer class=\"rodape\">\n  <div class=\"container\">")
            .append("DoaMais – Projeto Integrador II / LPOO · ODS 3 Saúde e Bem-Estar")
            .append("</div>\n</footer>\n")
            .append("</body>\n</html>\n");
        return html.toString();
    }

    /**
     * Caixa de mensagem colorida.
     *
     * @param tipo     "sucesso", "erro", "aviso" ou "info"
     * @param mensagem texto (sera escapado)
     */
    public static String alerta(String tipo, String mensagem) {
        return "<div class=\"alerta alerta-" + escapar(tipo) + "\" role=\"alert\">"
                + escapar(mensagem) + "</div>";
    }

    /** Barra de progresso horizontal com o percentual escrito (0 a 100). */
    public static String barraProgresso(double percentual) {
        long p = Math.round(Math.max(0, Math.min(100, percentual)));
        return "<div class=\"progresso\"><div class=\"progresso-barra\" style=\"width:" + p
                + "%\"></div><span>" + p + "%</span></div>";
    }

    /**
     * Card de campanha para o feed: nome do paciente, hospital, tipo sanguineo,
     * "3 de 5 bolsas", barra de progresso, link "Ver campanha"
     * (/campanhas/ver?id=) e botao "Quero doar" (/doacoes/nova?receptorId=).
     */
    public static String cardCampanha(Receptor r) {
        String tipo = r.getTipoSanguineo() == null ? "" : r.getTipoSanguineo().getRotulo();
        return "<article class=\"card card-campanha\">\n"
                + "  <div class=\"card-cabecalho\">\n"
                + "    <h3>" + escapar(r.getNome()) + "</h3>\n"
                + "    <span class=\"badge-tipo\">" + escapar(tipo) + "</span>\n"
                + "  </div>\n"
                + "  <p class=\"card-hospital\">🏥 " + escapar(r.getHospital()) + "</p>\n"
                + "  <p class=\"card-bolsas\"><strong>" + r.getDoacoesRecebidas() + " de "
                + r.getMetaDoacoes() + " bolsas</strong></p>\n"
                + "  " + barraProgresso(r.getProgresso()) + "\n"
                + "  <div class=\"card-acoes\">\n"
                + "    <a class=\"botao botao-secundario\" href=\"/campanhas/ver?id=" + r.getId()
                + "\">Ver campanha</a>\n"
                + "    <a class=\"botao\" href=\"/doacoes/nova?receptorId=" + r.getId()
                + "\">🩸 Quero doar</a>\n"
                + "  </div>\n"
                + "</article>";
    }

    /** Badge do termometro: tipo ("O-") com a cor e descricao do nivel. */
    public static String badgeEstoque(TipoSanguineo tipo, NivelEstoque nivel) {
        String classeCritico = nivel == NivelEstoque.CRITICO ? " critico" : "";
        return "<div class=\"badge-estoque" + classeCritico + "\" style=\"background:"
                + escapar(nivel.getCor()) + "\" title=\"" + escapar(tipo.getRotulo()) + ": "
                + escapar(nivel.getDescricao()) + "\">"
                + "<strong>" + escapar(tipo.getRotulo()) + "</strong>"
                + "<span>" + escapar(nivel.getDescricao()) + "</span></div>";
    }

    /** dd/MM/yyyy; null -> "—". */
    public static String formatarData(LocalDate data) {
        return data == null ? "—" : data.format(FORMATO_DATA);
    }

    /**
     * As 8 &lt;option value="A+"&gt;A+&lt;/option&gt; de um &lt;select&gt; de tipo
     * sanguineo, com 'selected' no tipo informado (null = nenhum).
     */
    public static String opcoesTipoSanguineo(TipoSanguineo selecionado) {
        StringBuilder html = new StringBuilder();
        for (TipoSanguineo t : TipoSanguineo.values()) {
            String rotulo = escapar(t.getRotulo());
            html.append("<option value=\"").append(rotulo).append("\"")
                .append(t == selecionado ? " selected" : "")
                .append(">").append(rotulo).append("</option>\n");
        }
        return html.toString();
    }
}
