package com.doamais.util;

import com.doamais.model.NivelEstoque;
import com.doamais.model.Receptor;
import com.doamais.model.TipoSanguineo;

import java.time.LocalDate;

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

    private HtmlUtil() {
    }

    /** Troca &amp; &lt; &gt; " ' pelas entidades HTML. null -> "". */
    public static String escapar(String texto) {
        // TODO(Maria)
        throw new UnsupportedOperationException("TODO(Maria): HtmlUtil.escapar");
    }

    /**
     * Pagina HTML completa: &lt;head&gt; com /static/style.css e /static/app.js,
     * cabecalho com logo e menu (Inicio /, Doadores /doadores, Pre-triagem /aptidao,
     * Nova campanha /campanhas/nova, Doacoes /doacoes, Exportar /exportar),
     * o conteudo e o rodape.
     *
     * @param titulo       titulo da aba e do &lt;h1&gt; (sera escapado)
     * @param conteudoHtml HTML ja pronto do corpo (NAO e escapado)
     */
    public static String pagina(String titulo, String conteudoHtml) {
        // TODO(Maria)
        throw new UnsupportedOperationException("TODO(Maria): HtmlUtil.pagina");
    }

    /**
     * Caixa de mensagem colorida.
     *
     * @param tipo     "sucesso", "erro", "aviso" ou "info"
     * @param mensagem texto (sera escapado)
     */
    public static String alerta(String tipo, String mensagem) {
        // TODO(Maria)
        throw new UnsupportedOperationException("TODO(Maria): HtmlUtil.alerta");
    }

    /** Barra de progresso horizontal com o percentual escrito (0 a 100). */
    public static String barraProgresso(double percentual) {
        // TODO(Maria)
        throw new UnsupportedOperationException("TODO(Maria): HtmlUtil.barraProgresso");
    }

    /**
     * Card de campanha para o feed: nome do paciente, hospital, tipo sanguineo,
     * "3 de 5 bolsas", barra de progresso, link "Ver campanha"
     * (/campanhas/ver?id=) e botao "Quero doar" (/doacoes/nova?receptorId=).
     */
    public static String cardCampanha(Receptor r) {
        // TODO(Maria)
        throw new UnsupportedOperationException("TODO(Maria): HtmlUtil.cardCampanha");
    }

    /** Badge do termometro: tipo ("O-") com a cor e descricao do nivel. */
    public static String badgeEstoque(TipoSanguineo tipo, NivelEstoque nivel) {
        // TODO(Maria)
        throw new UnsupportedOperationException("TODO(Maria): HtmlUtil.badgeEstoque");
    }

    /** dd/MM/yyyy; null -> "—". */
    public static String formatarData(LocalDate data) {
        // TODO(Maria)
        throw new UnsupportedOperationException("TODO(Maria): HtmlUtil.formatarData");
    }

    /**
     * As 8 &lt;option value="A+"&gt;A+&lt;/option&gt; de um &lt;select&gt; de tipo
     * sanguineo, com 'selected' no tipo informado (null = nenhum).
     */
    public static String opcoesTipoSanguineo(TipoSanguineo selecionado) {
        // TODO(Maria)
        throw new UnsupportedOperationException("TODO(Maria): HtmlUtil.opcoesTipoSanguineo");
    }
}
