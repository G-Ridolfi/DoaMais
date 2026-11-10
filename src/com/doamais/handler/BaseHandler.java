package com.doamais.handler;

import com.doamais.exception.DoaMaisException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
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

    @Override
    public void handle(HttpExchange ex) throws IOException {
        // TODO(Maria): implementar conforme o Javadoc da classe
        throw new UnsupportedOperationException("TODO(Maria): BaseHandler.handle");
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
        // TODO(Maria): responder 405
        throw new UnsupportedOperationException("TODO(Maria): BaseHandler.post");
    }

    /** Envia bytes com o status e Content-Type informados. */
    protected void enviarResposta(HttpExchange ex, int status, String contentType, byte[] corpo)
            throws IOException {
        // TODO(Maria): headers Content-Type, sendResponseHeaders, escrever corpo
        throw new UnsupportedOperationException("TODO(Maria): BaseHandler.enviarResposta");
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
        // TODO(Maria): header Location + sendResponseHeaders(303, -1)
        throw new UnsupportedOperationException("TODO(Maria): BaseHandler.redirecionar");
    }

    /** @return valor do parametro da URL (?nome=valor), ou null se ausente */
    protected String getParametro(HttpExchange ex, String nome) {
        // TODO(Maria): FormUtil.parseQuery(ex.getRequestURI()).get(nome)
        throw new UnsupportedOperationException("TODO(Maria): BaseHandler.getParametro");
    }

    /** @return caminho da URL sem a query, ex: "/doadores/novo" */
    protected String getCaminho(HttpExchange ex) {
        return ex.getRequestURI().getPath();
    }
}
