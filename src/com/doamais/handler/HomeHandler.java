package com.doamais.handler;

import com.doamais.exception.DoaMaisException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

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
public class HomeHandler extends BaseHandler {

    @Override
    protected void get(HttpExchange ex) throws DoaMaisException, IOException {
        // TODO(Maria): se getCaminho(ex) != "/" -> 404; senao montar a pagina descrita acima
        throw new UnsupportedOperationException("TODO(Maria): HomeHandler.get");
    }
}
