package com.doamais.handler;

import com.doamais.exception.DoaMaisException;
import com.doamais.util.FormUtil;
import com.doamais.util.HtmlUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Classe ABSTRATA que todos os handlers (controladores web) herdam.
 *
 * HERANCA: HomeHandler, DoadorHandler, AptidaoHandler, ReceptorHandler,
 * DoacaoHandler e ExportarHandler estendem BaseHandler.
 * POLIMORFISMO (padrao Template Method): handle() e escrito uma unica vez
 * aqui e chama get()/post(), que cada handler sobrescreve.
 *
 * Comportamento esperado de handle():
 * <ul>
 *   <li>GET  -> get(ex)</li>
 *   <li>POST -> post(ex, FormUtil.parse(FormUtil.lerCorpo(ex)))</li>
 *   <li>outro metodo -> 405</li>
 *   <li>catch (DoaMaisException e): status 400 com HtmlUtil.pagina("Atenção",
 *       HtmlUtil.alerta("erro", e.getMessage()) + link "Voltar")</li>
 *   <li>catch (Exception e): status 500 com pagina de erro generica e
 *       e.printStackTrace() no console. O servidor NUNCA cai.</li>
 *   <li>finally: ex.close()</li>
 * </ul>
 *
 * Um handler atende um PREFIXO de rota (ex: "/doadores" atende tambem
 * "/doadores/novo"). Use getCaminho(ex) para decidir o que mostrar e
 * responda 404 (enviarHtml com status 404) para caminhos desconhecidos.
 *
 * Responsavel: Maria Vitoria.
 * CONTRATO: assinaturas usadas por todos. Nao alterar sem avisar o grupo.
 */
public abstract class BaseHandler implements HttpHandler {

    /** Botao "Voltar" usado nas paginas de erro (volta para o formulario preenchido). */
    private static final String BOTAO_VOLTAR =
            "<p><a class=\"botao\" href=\"javascript:history.back()\">&larr; Voltar</a></p>";

    /**
     * TEMPLATE METHOD: o "esqueleto" do atendimento e sempre o mesmo; so os
     * passos get()/post() mudam. POLIMORFISMO: em tempo de execucao, get(ex)
     * executa a versao sobrescrita do handler concreto (HomeHandler,
     * DoadorHandler, ...), sem que esta classe saiba qual e.
     */
    @Override
    public void handle(HttpExchange ex) throws IOException {
        try {
            String metodo = ex.getRequestMethod().toUpperCase();
            if ("GET".equals(metodo)) {
                get(ex);                                            // polimorfismo
            } else if ("POST".equals(metodo)) {
                post(ex, FormUtil.parse(FormUtil.lerCorpo(ex)));    // polimorfismo
            } else {
                enviarMetodoNaoPermitido(ex);
            }
        } catch (DoaMaisException e) {
            // TRATAMENTO DE EXCECAO: ValidacaoException, DAOException e
            // DoadorInaptoException herdam de DoaMaisException e viram um alerta.
            if (respostaAindaNaoEnviada(ex)) {
                enviarHtml(ex, 400, HtmlUtil.pagina("Atenção",
                        HtmlUtil.alerta("erro", e.getMessage()) + BOTAO_VOLTAR));
            }
        } catch (Exception e) {
            // Erro inesperado (bug, TODO de colega, etc.): registra no console e
            // responde 500. O servidor continua de pe para as proximas requisicoes.
            e.printStackTrace();
            if (respostaAindaNaoEnviada(ex)) {
                enviarHtml(ex, 500, HtmlUtil.pagina("Ops! Algo deu errado.",
                        HtmlUtil.alerta("erro", "Ocorreu um erro inesperado. Tente novamente em instantes.")
                                + BOTAO_VOLTAR));
            }
        } finally {
            ex.close();
        }
    }

    /** Responde a um GET. Todo handler implementa. */
    protected abstract void get(HttpExchange ex) throws DoaMaisException, IOException;

    /**
     * Responde a um POST de formulario. Padrao: 405 (metodo nao permitido).
     * Handlers com formulario sobrescrevem.
     *
     * @param form campos do formulario ja decodificados
     */
    protected void post(HttpExchange ex, Map<String, String> form) throws DoaMaisException, IOException {
        enviarMetodoNaoPermitido(ex);
    }

    /** Envia bytes com o status e Content-Type informados. */
    protected void enviarResposta(HttpExchange ex, int status, String contentType, byte[] corpo)
            throws IOException {
        ex.getResponseHeaders().set("Content-Type", contentType);
        ex.sendResponseHeaders(status, corpo.length);
        try (OutputStream saida = ex.getResponseBody()) {
            saida.write(corpo);
        }
    }

    /** Envia uma pagina HTML (UTF-8) com o status informado. */
    protected void enviarHtml(HttpExchange ex, int status, String html) throws IOException {
        enviarResposta(ex, status, "text/html; charset=UTF-8", html.getBytes(StandardCharsets.UTF_8));
    }

    /** Envia uma pagina HTML com status 200. */
    protected void enviarHtml(HttpExchange ex, String html) throws IOException {
        enviarHtml(ex, 200, html);
    }

    /**
     * Redireciona o navegador (303 See Other). Usar apos um POST bem-sucedido
     * (padrao Post/Redirect/Get), ex: redirecionar(ex, "/campanhas/ver?id=3").
     */
    protected void redirecionar(HttpExchange ex, String url) throws IOException {
        ex.getResponseHeaders().set("Location", url);
        ex.sendResponseHeaders(303, -1);
    }

    /** @return valor do parametro da URL (?nome=valor), ou null se ausente */
    protected String getParametro(HttpExchange ex, String nome) {
        return FormUtil.parseQuery(ex.getRequestURI()).get(nome);
    }

    /** @return caminho da URL sem a query, ex: "/doadores/novo" */
    protected String getCaminho(HttpExchange ex) {
        return ex.getRequestURI().getPath();
    }

    /** Pagina 405 (metodo HTTP nao suportado por esta rota). */
    private void enviarMetodoNaoPermitido(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().set("Allow", "GET, POST");
        enviarHtml(ex, 405, HtmlUtil.pagina("Método não permitido",
                HtmlUtil.alerta("aviso", "Esta página não aceita o método " + ex.getRequestMethod() + ".")
                        + BOTAO_VOLTAR));
    }

    /** @return true se nenhum status foi enviado ainda (getResponseCode() == -1) */
    private boolean respostaAindaNaoEnviada(HttpExchange ex) {
        return ex.getResponseCode() == -1;
    }
}
