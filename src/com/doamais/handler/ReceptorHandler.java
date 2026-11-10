package com.doamais.handler;

import com.doamais.exception.DoaMaisException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

/**
 * Telas de Campanhas (Receptores). Prefixo: "/campanhas".
 *
 * Rotas:
 * <ul>
 *   <li>GET  /campanhas/nova    formulario: nomePaciente, hospital, tipoSanguineo,
 *                               metaDoacoes, historia</li>
 *   <li>POST /campanhas/nova    cria o Receptor (construtor de campanha nova),
 *                               validar(), ReceptorDAO.inserir, redireciona para
 *                               /campanhas/ver?id=NOVO_ID</li>
 *   <li>GET  /campanhas/ver?id= pagina da campanha: historia, hospital, tipo,
 *                               barra de progresso, "faltam X bolsas", status, e
 *                               botao "Quero doar" (/doacoes/nova?receptorId=ID)
 *                               (oculto se concluida)</li>
 * </ul>
 * O FEED de campanhas fica na home (HomeHandler, da Maria).
 *
 * Responsavel: Ricardo.
 */
public class ReceptorHandler extends BaseHandler {

    @Override
    protected void get(HttpExchange ex) throws DoaMaisException, IOException {
        // TODO(Ricardo)
        throw new UnsupportedOperationException("TODO(Ricardo): ReceptorHandler.get");
    }

    @Override
    protected void post(HttpExchange ex, Map<String, String> form) throws DoaMaisException, IOException {
        // TODO(Ricardo)
        throw new UnsupportedOperationException("TODO(Ricardo): ReceptorHandler.post");
    }
}
