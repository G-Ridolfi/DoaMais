package com.doamais.util;

import com.doamais.exception.ValidacaoException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
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
// #16 Classe utilitaria final (construtor private na linha 50): apenas metodos static, sem new
public final class FormUtil {

    /**
     * COLECAO (Map): nome tecnico do campo -> nome amigavel usado nas
     * mensagens de erro. Campos fora do mapa aparecem com o proprio nome.
     */
    // #17 Colecao (LinkedHashMap): traduz nomes tecnicos para texto legivel nas mensagens de erro
    private static final Map<String, String> NOMES_AMIGAVEIS = new LinkedHashMap<>();

    static {
        NOMES_AMIGAVEIS.put("dataNascimento", "data de nascimento");
        NOMES_AMIGAVEIS.put("dataUltimaDoacao", "data da última doação");
        NOMES_AMIGAVEIS.put("dataDoacao", "data da doação");
        NOMES_AMIGAVEIS.put("tipoSanguineo", "tipo sanguíneo");
        NOMES_AMIGAVEIS.put("nomePaciente", "nome do paciente");
        NOMES_AMIGAVEIS.put("metaDoacoes", "meta de doações");
        NOMES_AMIGAVEIS.put("doadorId", "doador");
        NOMES_AMIGAVEIS.put("receptorId", "campanha");
        NOMES_AMIGAVEIS.put("historia", "história");
    }

    private FormUtil() {
    }

    // #18 Leitura: le os dados brutos enviados pelo formulario em UTF-8
    public static String lerCorpo(HttpExchange ex) throws IOException {
        return new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Converte "a=1&b=Jo%C3%A3o" em um mapa {a=1, b=João}.
     * Usa URLDecoder com UTF-8. Mantem a ordem dos campos (LinkedHashMap).
     *
     * @param dados corpo do POST ou query string; null/vazio -> mapa vazio
     */
    public static Map<String, String> parse(String dados) {
        // #19 Colecao: parser manual (& e =) em LinkedHashMap para manter a ordem dos campos
        Map<String, String> campos = new LinkedHashMap<>();
        if (dados == null || dados.isEmpty()) {
            return campos;
        }
        // 1) cada par "chave=valor" e separado por '&'
        for (String par : dados.split("&")) {
            if (par.isEmpty()) {
                continue; // ignora pares vazios (ex: "a=1&&b=2")
            }
            // 2) separa pelo PRIMEIRO '=' (o valor pode conter '=' codificado ou nao)
            int igual = par.indexOf('=');
            String chave = igual >= 0 ? par.substring(0, igual) : par;
            String valor = igual >= 0 ? par.substring(igual + 1) : "";
            // 3) decodifica %XX e '+' (espaco) usando UTF-8
            chave = URLDecoder.decode(chave, StandardCharsets.UTF_8);
            valor = URLDecoder.decode(valor, StandardCharsets.UTF_8);
            if (!chave.isEmpty()) {
                campos.put(chave, valor);
            }
        }
        return campos;
    }

    /** Parametros da URL (ex: /campanhas/ver?id=3 -> {id=3}). */
    public static Map<String, String> parseQuery(URI uri) {
        return parse(uri.getRawQuery());
    }

    /**
     * @return texto sem espacos nas pontas; null se vazio e nao obrigatorio
     * @throws ValidacaoException se obrigatorio e vazio
     */
    public static String getTexto(Map<String, String> form, String campo, boolean obrigatorio)
            throws ValidacaoException {
        String valor = form.get(campo);
        valor = (valor == null) ? "" : valor.trim();
        if (valor.isEmpty()) {
            if (obrigatorio) {
                throw new ValidacaoException("O campo '" + nomeAmigavel(campo) + "' é obrigatório.");
            }
            return null;
        }
        return valor;
    }

    /** Campo inteiro obrigatorio. */
    public static int getInt(Map<String, String> form, String campo) throws ValidacaoException {
        String valor = getTexto(form, campo, true);
        return converterInt(valor, campo);
    }

    /** Campo inteiro opcional: null se vazio (ex: receptorId = estoque geral). */
    public static Integer getIntOpcional(Map<String, String> form, String campo)
            throws ValidacaoException {
        String valor = getTexto(form, campo, false);
        if (valor == null) {
            return null;
        }
        return converterInt(valor, campo);
    }

    /** Campo decimal obrigatorio. Aceita virgula ou ponto ("72,5" ou "72.5"). */
    public static double getDouble(Map<String, String> form, String campo) throws ValidacaoException {
        String valor = getTexto(form, campo, true);
        try {
            return Double.parseDouble(valor.replace(',', '.'));
        } catch (NumberFormatException e) {
            // #20 Tratamento de excecao: centraliza conversoes e lanca ValidacaoException com mensagem clara
            throw new ValidacaoException("O campo '" + nomeAmigavel(campo) + "' deve ser um número.", e);
        }
    }

    /**
     * Campo de data no formato do input type="date" (yyyy-MM-dd).
     *
     * @return a data; null se vazio e nao obrigatorio
     */
    public static LocalDate getData(Map<String, String> form, String campo, boolean obrigatorio)
            throws ValidacaoException {
        String valor = getTexto(form, campo, obrigatorio);
        if (valor == null) {
            return null;
        }
        try {
            LocalDate data = LocalDate.parse(valor);
            if (data.getYear() < 1900 || data.getYear() > 2100) {
                throw new ValidacaoException("O campo '" + nomeAmigavel(campo)
                        + "' possui um ano inválido (" + data.getYear() + "). Informe uma data real.");
            }
            if (data.isAfter(LocalDate.now())) {
                throw new ValidacaoException("O campo '" + nomeAmigavel(campo)
                        + "' não pode ser uma data futura.");
            }
            return data;
        } catch (DateTimeParseException e) {
            throw new ValidacaoException("O campo '" + nomeAmigavel(campo)
                    + "' deve ser uma data válida (dd/mm/aaaa).", e);
        }
    }

    /** Converte texto em int, lancando ValidacaoException com a causa original. */
    private static int converterInt(String valor, String campo) throws ValidacaoException {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new ValidacaoException("O campo '" + nomeAmigavel(campo)
                    + "' deve ser um número inteiro.", e);
        }
    }

    /** @return nome legivel do campo para mensagens (ex: dataNascimento -> data de nascimento) */
    private static String nomeAmigavel(String campo) {
        return NOMES_AMIGAVEIS.getOrDefault(campo, campo);
    }
}
