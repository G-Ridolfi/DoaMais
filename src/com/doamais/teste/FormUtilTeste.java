package com.doamais.teste;

import com.doamais.exception.ValidacaoException;
import com.doamais.util.FormUtil;
import com.doamais.util.HtmlUtil;

import java.time.LocalDate;
import java.util.Map;

/**
 * Teste manual do FormUtil (parser de formulario feito a mao) e do HtmlUtil.
 * Nao depende do banco nem dos colegas.
 *
 * Rodar: build.bat com.doamais.teste.FormUtilTeste
 *
 * Responsavel: Maria Vitoria.
 */
public class FormUtilTeste {

    private static int falhas = 0;

    // #33 Teste isolado do modulo: valida parse, ValidacaoException e escapar anti-XSS
    public static void main(String[] args) {
        System.out.println("=== FormUtilTeste ===\n");

        // 1) parse: URL decoding (%C3%A3 = ã, + = espaco, %2C = virgula) e campo vazio
        Map<String, String> form = FormUtil.parse("nome=Jo%C3%A3o+Silva&peso=72%2C5&vazio=");
        System.out.println("parse            -> " + form);
        conferir("parse", "{nome=João Silva, peso=72,5, vazio=}", form.toString());
        conferir("parse(null)", "{}", FormUtil.parse(null).toString());
        conferir("parse('a=1&&b=x=y')", "{a=1, b=x=y}", FormUtil.parse("a=1&&b=x=y").toString());

        // 2) getDouble aceita virgula
        try {
            double peso = FormUtil.getDouble(form, "peso");
            System.out.println("getDouble(72,5)  -> " + peso);
            conferir("getDouble", "72.5", String.valueOf(peso));
        } catch (ValidacaoException e) {
            falhar("getDouble lancou: " + e.getMessage());
        }

        // 3) getInt com texto invalido -> ValidacaoException (com a causa original)
        try {
            FormUtil.getInt(Map.of("idade", "abc"), "idade");
            falhar("getInt('abc') deveria lancar ValidacaoException");
        } catch (ValidacaoException e) {
            System.out.println("getInt('abc')    -> " + e.getMessage()
                    + " (causa: " + e.getCause().getClass().getSimpleName() + ")");
        }

        // 4) getTexto obrigatorio vazio -> ValidacaoException
        try {
            FormUtil.getTexto(form, "vazio", true);
            falhar("getTexto obrigatorio vazio deveria lancar ValidacaoException");
        } catch (ValidacaoException e) {
            System.out.println("getTexto(vazio)  -> " + e.getMessage());
        }

        // 5) getIntOpcional vazio -> null; getData invalida -> ValidacaoException
        try {
            Integer receptor = FormUtil.getIntOpcional(Map.of("receptorId", ""), "receptorId");
            conferir("getIntOpcional vazio", "null", String.valueOf(receptor));
            FormUtil.getData(Map.of("dataNascimento", "31/12/2000"), "dataNascimento", true);
            falhar("getData('31/12/2000') deveria lancar ValidacaoException");
        } catch (ValidacaoException e) {
            System.out.println("getData invalida -> " + e.getMessage());
        }

        // 6) HtmlUtil
        String escapado = HtmlUtil.escapar("<script>alert('x')</script>");
        System.out.println("escapar          -> " + escapado);
        conferir("escapar", "&lt;script&gt;alert(&#39;x&#39;)&lt;/script&gt;", escapado);

        String barra = HtmlUtil.barraProgresso(60);
        System.out.println("barraProgresso   -> " + barra);
        conferir("barraProgresso", "<div class=\"progresso\"><div class=\"progresso-barra\" "
                + "style=\"width:60%\"></div><span>60%</span></div>", barra);

        String data = HtmlUtil.formatarData(LocalDate.of(2026, 11, 23));
        System.out.println("formatarData     -> " + data);
        conferir("formatarData", "23/11/2026", data);
        conferir("formatarData(null)", "—", HtmlUtil.formatarData(null));

        System.out.println();
        System.out.println(falhas == 0 ? "TODOS OS TESTES PASSARAM." : falhas + " FALHA(S).");
    }

    /** Compara o esperado com o obtido e conta a falha, se houver. */
    private static void conferir(String nome, String esperado, String obtido) {
        if (!esperado.equals(obtido)) {
            falhar(nome + ": esperado [" + esperado + "] mas veio [" + obtido + "]");
        }
    }

    private static void falhar(String mensagem) {
        falhas++;
        System.out.println("  FALHOU: " + mensagem);
    }
}
