package com.doamais.handler;

import com.doamais.exception.DoaMaisException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

/**
 * Pre-triagem de aptidao. Prefixo: "/aptidao".
 *
 * Rotas:
 * <ul>
 *   <li>GET  /aptidao            formulario (funciona SEM cadastro): dataNascimento,
 *                                sexo (M/F), peso, dataUltimaDoacao (opcional)</li>
 *   <li>GET  /aptidao?doadorId=3 avalia um doador ja cadastrado (DoadorDAO.buscarPorId)</li>
 *   <li>POST /aptidao            monta um Doador temporario com os campos, chama
 *                                MotorAptidao.avaliar() e mostra o resultado</li>
 * </ul>
 * Resultado: alerta "sucesso" (Apto para doar!) ou "erro" com a lista de
 * motivos e "Liberado a partir de dd/mm/aaaa" quando houver data.
 *
 * Responsavel: Guilherme.
 */
public class AptidaoHandler extends BaseHandler {

    @Override
    protected void get(HttpExchange ex) throws DoaMaisException, IOException {
        // TODO(Guilherme)
        throw new UnsupportedOperationException("TODO(Guilherme): AptidaoHandler.get");
    }

    @Override
    protected void post(HttpExchange ex, Map<String, String> form) throws DoaMaisException, IOException {
        // TODO(Guilherme)
        throw new UnsupportedOperationException("TODO(Guilherme): AptidaoHandler.post");
    }
}
