package com.doamais.util;

import com.doamais.exception.ValidacaoException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.Map;

/**
 * Leitura e conversao de formularios HTTP, feita "na mao" (sem bibliotecas).
 * Desafio tecnico citado nos slides.
 *
 * Os formularios do projeto usam method="post" e o formato padrao
 * application/x-www-form-urlencoded (ex: "nome=Jo%C3%A3o&peso=72%2C5").
 *
 * Os metodos get*() convertem e VALIDAM campos, lancando ValidacaoException
 * com mensagem amigavel (ex: "O campo 'peso' deve ser um número.").
 * TODOS os handlers devem usa-los em vez de converter por conta propria.
 *
 * Responsavel: Maria Vitoria.
 * CONTRATO: usado por todos os handlers. Nao alterar assinaturas sem avisar.
 */
public final class FormUtil {

    private FormUtil() {
    }

    /** Le o corpo inteiro da requisicao como texto UTF-8. */
    public static String lerCorpo(HttpExchange ex) throws IOException {
        // TODO(Maria): new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8)
        throw new UnsupportedOperationException("TODO(Maria): FormUtil.lerCorpo");
    }

    /**
     * Converte "a=1&b=Jo%C3%A3o" em um mapa {a=1, b=João}.
     * Usa URLDecoder com UTF-8. Mantem a ordem dos campos (LinkedHashMap).
     *
     * @param dados corpo do POST ou query string; null/vazio -> mapa vazio
     */
    public static Map<String, String> parse(String dados) {
        // TODO(Maria)
        throw new UnsupportedOperationException("TODO(Maria): FormUtil.parse");
    }

    /** Parametros da URL (ex: /campanhas/ver?id=3 -> {id=3}). */
    public static Map<String, String> parseQuery(URI uri) {
        // TODO(Maria): parse(uri.getRawQuery())
        throw new UnsupportedOperationException("TODO(Maria): FormUtil.parseQuery");
    }

    /**
     * @return texto sem espacos nas pontas; null se vazio e nao obrigatorio
     * @throws ValidacaoException se obrigatorio e vazio
     */
    public static String getTexto(Map<String, String> form, String campo, boolean obrigatorio)
            throws ValidacaoException {
        // TODO(Maria)
        throw new UnsupportedOperationException("TODO(Maria): FormUtil.getTexto");
    }

    /** Campo inteiro obrigatorio. */
    public static int getInt(Map<String, String> form, String campo) throws ValidacaoException {
        // TODO(Maria)
        throw new UnsupportedOperationException("TODO(Maria): FormUtil.getInt");
    }

    /** Campo inteiro opcional: null se vazio (ex: receptorId = estoque geral). */
    public static Integer getIntOpcional(Map<String, String> form, String campo)
            throws ValidacaoException {
        // TODO(Maria)
        throw new UnsupportedOperationException("TODO(Maria): FormUtil.getIntOpcional");
    }

    /** Campo decimal obrigatorio. Aceita virgula ou ponto ("72,5" ou "72.5"). */
    public static double getDouble(Map<String, String> form, String campo) throws ValidacaoException {
        // TODO(Maria)
        throw new UnsupportedOperationException("TODO(Maria): FormUtil.getDouble");
    }

    /**
     * Campo de data no formato do input type="date" (yyyy-MM-dd).
     *
     * @return a data; null se vazio e nao obrigatorio
     */
    public static LocalDate getData(Map<String, String> form, String campo, boolean obrigatorio)
            throws ValidacaoException {
        // TODO(Maria)
        throw new UnsupportedOperationException("TODO(Maria): FormUtil.getData");
    }
}
