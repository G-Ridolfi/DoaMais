package com.doamais.service;

import com.doamais.model.Doador;

import java.time.LocalDate;

/**
 * Regra de aptidao por faixa etaria segundo o Ministerio da Saude
 * (Portaria de Consolidacao n. 5/2017): o candidato deve ter entre
 * 18 e 69 anos completos na data da doacao.
 *
 * <ul>
 *   <li>Menor de 18 anos: inapto, com data de liberacao no 18o aniversario;</li>
 *   <li>Maior de 69 anos: inapto definitivamente (data de liberacao nula);</li>
 *   <li>De 18 a 69 anos: apto.</li>
 * </ul>
 *
 * POLIMORFISMO: implementa a interface {@link RegraAptidao}, permitindo que
 * o {@link MotorAptidao} avalie esta regra de forma uniforme junto com as demais.
 *
 * Responsavel: Guilherme.
 */
public class RegraIdade implements RegraAptidao {

    /** Idade minima oficial para doacao (em anos completos). */
    public static final int IDADE_MINIMA = 18;

    /** Idade maxima oficial para doacao (em anos completos). */
    public static final int IDADE_MAXIMA = 69;

    /**
     * Avalia se a idade do doador na data de referencia esta entre 18 e 69 anos.
     *
     * @param doador doador a ser avaliado
     * @param hoje   data de referencia
     * @return resultado apto ou inapto (com data do 18o aniversario se menor de idade)
     */
    @Override
    public ResultadoAptidao avaliar(Doador doador, LocalDate hoje) {
        int idade = doador.getIdade(hoje);

        if (idade < IDADE_MINIMA) {
            LocalDate dataLiberacao = doador.getDataNascimento() != null
                    ? doador.getDataNascimento().plusYears(IDADE_MINIMA)
                    : null;
            return ResultadoAptidao.inapto("Idade mínima para doar é 18 anos", dataLiberacao);
        }

        if (idade > IDADE_MAXIMA) {
            return ResultadoAptidao.inapto("Idade máxima para doar é 69 anos", null);
        }

        return ResultadoAptidao.apto();
    }
}
