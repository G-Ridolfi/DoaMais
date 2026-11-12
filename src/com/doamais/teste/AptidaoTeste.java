package com.doamais.teste;

import com.doamais.exception.DoadorInaptoException;
import com.doamais.model.Doador;
import com.doamais.model.Sexo;
import com.doamais.model.TipoSanguineo;
import com.doamais.service.MotorAptidao;
import com.doamais.service.ResultadoAptidao;
import com.doamais.util.HtmlUtil;

import java.time.LocalDate;

/**
 * Teste do Motor de Regras de Aptidao (Guilherme).
 *
 * Demonstra:
 * 1. Polimorfismo via interface RegraAptidao (RegraIdade, RegraPeso, RegraIntervalo);
 * 2. Uso de colecoes (List<RegraAptidao> e List<String> de motivos);
 * 3. Calculo de datas de liberacao com LocalDate.plusDays/plusYears e ChronoUnit.DAYS.between;
 * 4. Tratamento de excecao propria com dados extras (DoadorInaptoException).
 *
 * Rodar: build.bat com.doamais.teste.AptidaoTeste
 */
public class AptidaoTeste {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  INICIANDO TESTES DO MOTOR DE APTIDÃO (GUILHERME)");
        System.out.println("==================================================\n");

        LocalDate hoje = LocalDate.of(2026, 11, 23);
        MotorAptidao motor = new MotorAptidao();

        // Cenario 1: Homem, 35 anos, 80 kg, doou em 2026-10-29 (ha 25 dias)
        // Esperado: inapto, liberado em 28/12/2026
        Doador c1 = new Doador(
                1, "Carlos Eduardo", TipoSanguineo.O_NEG, "11122233344",
                hoje.minusYears(35), Sexo.MASCULINO, 80.0,
                "(27) 99999-1111", "carlos@email.com", LocalDate.of(2026, 10, 29)
        );
        ResultadoAptidao r1 = testarCenario("1. Homem, 35 anos, 80 kg, doou em 2026-10-29 (há 25 dias)", motor, c1, hoje);
        validar(!r1.isApto() && LocalDate.of(2026, 12, 28).equals(r1.getDataLiberacao()),
                "Cenário 1 deveria ser inapto com liberação em 28/12/2026");

        // Cenario 2: Mulher, 28 anos, 62 kg, nunca doou
        // Esperado: apto
        Doador c2 = new Doador(
                2, "Ana Beatriz", TipoSanguineo.A_POS, "22233344455",
                hoje.minusYears(28), Sexo.FEMININO, 62.0,
                "(27) 99999-2222", "ana@email.com", null
        );
        ResultadoAptidao r2 = testarCenario("2. Mulher, 28 anos, 62 kg, nunca doou", motor, c2, hoje);
        validar(r2.isApto() && r2.getMotivos().isEmpty() && r2.getDataLiberacao() == null,
                "Cenário 2 deveria ser apto");

        // Cenario 3: 17 anos, 70 kg
        // Esperado: inapto, liberado no 18o aniversario
        LocalDate nasc17 = LocalDate.of(2009, 5, 15);
        Doador c3 = new Doador(
                3, "Pedro Jovem", TipoSanguineo.B_POS, "33344455566",
                nasc17, Sexo.MASCULINO, 70.0,
                null, null, null
        );
        ResultadoAptidao r3 = testarCenario("3. 17 anos, 70 kg", motor, c3, hoje);
        validar(!r3.isApto() && nasc17.plusYears(18).equals(r3.getDataLiberacao()),
                "Cenário 3 deveria ser inapto com liberação no 18º aniversário");

        // Cenario 4: 70 anos, 75 kg
        // Esperado: inapto, sem data
        Doador c4 = new Doador(
                4, "Roberto Sênior", TipoSanguineo.O_POS, "44455566677",
                hoje.minusYears(70), Sexo.MASCULINO, 75.0,
                null, null, null
        );
        ResultadoAptidao r4 = testarCenario("4. 70 anos, 75 kg", motor, c4, hoje);
        validar(!r4.isApto() && r4.getDataLiberacao() == null,
                "Cenário 4 deveria ser inapto sem data de liberação");

        // Cenario 5: 30 anos, 45 kg
        // Esperado: inapto (peso), sem data
        Doador c5 = new Doador(
                5, "Mariana Leve", TipoSanguineo.AB_POS, "55566677788",
                hoje.minusYears(30), Sexo.FEMININO, 45.0,
                null, null, null
        );
        ResultadoAptidao r5 = testarCenario("5. 30 anos, 45 kg", motor, c5, hoje);
        validar(!r5.isApto() && r5.getDataLiberacao() == null,
                "Cenário 5 deveria ser inapto (peso) sem data de liberação");

        // Cenario 6: Mulher, doou ha 60 dias
        // Esperado: inapto (precisa 90)
        LocalDate doouHa60Dias = hoje.minusDays(60);
        Doador c6 = new Doador(
                6, "Fernanda Lima", TipoSanguineo.A_NEG, "66677788899",
                hoje.minusYears(25), Sexo.FEMININO, 60.0,
                null, null, doouHa60Dias
        );
        ResultadoAptidao r6 = testarCenario("6. Mulher, doou há 60 dias", motor, c6, hoje);
        validar(!r6.isApto() && doouHa60Dias.plusDays(90).equals(r6.getDataLiberacao()),
                "Cenário 6 deveria ser inapto (mulher precisa aguardar 90 dias)");

        // Cenario 7: 17 anos E 45 kg
        // Esperado: inapto com 2 motivos
        Doador c7 = new Doador(
                7, "Júlia Menor", TipoSanguineo.O_NEG, "77788899900",
                nasc17, Sexo.FEMININO, 45.0,
                null, null, null
        );
        ResultadoAptidao r7 = testarCenario("7. 17 anos e 45 kg", motor, c7, hoje);
        validar(!r7.isApto() && r7.getMotivos().size() == 2 && nasc17.plusYears(18).equals(r7.getDataLiberacao()),
                "Cenário 7 deveria ser inapto com 2 motivos");

        // Teste de verificarOuLancar com try/catch no primeiro cenario
        System.out.println("--- Teste de verificarOuLancar (DoadorInaptoException) ---");
        try {
            motor.verificarOuLancar(c1);
            throw new AssertionError("Deveria ter lançado DoadorInaptoException para o cenário 1!");
        } catch (DoadorInaptoException e) {
            System.out.println("[OK - Exceção capturada] " + e.getMessage());
            System.out.println("  Motivos: " + e.getMotivos());
            System.out.println("  Data de liberação: " + HtmlUtil.formatarData(e.getDataLiberacao()));
        }

        System.out.println("\n==================================================");
        System.out.println("  TODOS OS TESTES DE APTIDÃO PASSARAM COM SUCESSO!");
        System.out.println("==================================================");
    }

    private static ResultadoAptidao testarCenario(String titulo, MotorAptidao motor, Doador doador, LocalDate hoje) {
        System.out.println("--- " + titulo + " ---");
        ResultadoAptidao r = motor.avaliar(doador, hoje);
        System.out.println("  Status: " + (r.isApto() ? "APTO" : "INAPTO"));
        if (!r.isApto()) {
            System.out.println("  Motivos (" + r.getMotivos().size() + "): " + String.join(" | ", r.getMotivos()));
            System.out.println("  Liberação: " + HtmlUtil.formatarData(r.getDataLiberacao()));
        }
        System.out.println();
        return r;
    }

    private static void validar(boolean condicao, String mensagemErro) {
        if (!condicao) {
            throw new AssertionError(mensagemErro);
        }
    }
}
