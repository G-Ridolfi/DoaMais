package com.doamais.handler;

import com.doamais.exception.DoaMaisException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

/**
 * Telas do modulo de Doadores. Prefixo: "/doadores".
 *
 * Rotas:
 * <ul>
 *   <li>GET  /doadores            lista (tabela) com filtro ?tipo=O- (select de tipo)</li>
 *   <li>GET  /doadores/novo       formulario de cadastro</li>
 *   <li>POST /doadores/novo       cria o Doador, chama validar(), DoadorDAO.inserir,
 *                                 redireciona para /doadores</li>
 *   <li>GET  /doadores/editar?id= formulario preenchido</li>
 *   <li>POST /doadores/editar     atualiza (campo oculto "id"), validar(), DoadorDAO.atualizar</li>
 * </ul>
 * Campos do formulario (atributo name): nome, cpf, dataNascimento, sexo (M/F),
 * peso, tipoSanguineo (A+...), telefone, email, dataUltimaDoacao.
 * Converter com FormUtil.get*(); erros sobem como ValidacaoException.
 *
 * Responsavel: Pedro.
 */
public class DoadorHandler extends BaseHandler {

    @Override
    protected void get(HttpExchange ex) throws DoaMaisException, IOException {
        // TODO(Pedro)
        throw new UnsupportedOperationException("TODO(Pedro): DoadorHandler.get");
    }

    @Override
    protected void post(HttpExchange ex, Map<String, String> form) throws DoaMaisException, IOException {
        // TODO(Pedro)
        throw new UnsupportedOperationException("TODO(Pedro): DoadorHandler.post");
    }
}
