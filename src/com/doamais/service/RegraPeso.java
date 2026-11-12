package com.doamais.service;

import com.doamais.model.Doador;

import java.time.LocalDate;

/**
 * Regra de aptidao por peso corporal segundo o Ministerio da Saude
 * (Portaria de Consolidacao n. 5/2017): o doador deve pesar no minimo 50 kg.
 *
 * <p>Caso o peso seja inferior a 50 kg, o candidato e considerado inapto sem
 * data prevista de liberacao ({@code null}), pois a aptidao depende do ganho de peso.</p>
 *
 * POLIMORFISMO: implementa a interface {@link RegraAptidao}.
 *
 * Responsavel: Guilherme.
 */
public class RegraPeso implements RegraAptidao {

    /** Peso minimo oficial para doacao de sangue (em quilogramas). */
    public static final double PESO_MINIMO = 50.0;

    /**
     * Avalia se o doador possui peso igual ou superior a 50 kg.
     *
     * @param doador doador a ser avaliado
     * @param hoje   data de referencia
     * @return resultado apto ou inapto (sem data de liberacao)
     */
    @Override
    public ResultadoAptidao avaliar(Doador doador, LocalDate hoje) {
        if (doador.getPeso() < PESO_MINIMO) {
            return ResultadoAptidao.inapto("Peso mínimo para doar é 50 kg", null);
        }
        return ResultadoAptidao.apto();
    }
}
