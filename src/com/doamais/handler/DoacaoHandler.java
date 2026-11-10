package com.doamais.handler;

import com.doamais.exception.DoaMaisException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
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

    @Override
    protected void get(HttpExchange ex) throws DoaMaisException, IOException {
        // TODO(Amon)
        throw new UnsupportedOperationException("TODO(Amon): DoacaoHandler.get");
    }

    @Override
    protected void post(HttpExchange ex, Map<String, String> form) throws DoaMaisException, IOException {
        // TODO(Amon)
        throw new UnsupportedOperationException("TODO(Amon): DoacaoHandler.post");
    }
}
