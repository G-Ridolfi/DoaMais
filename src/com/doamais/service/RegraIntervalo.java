package com.doamais.service;

import com.doamais.model.Doador;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Regra de aptidao por intervalo minimo entre doacoes segundo o Ministerio da Saude
 * (Portaria de Consolidacao n. 5/2017):
 * <ul>
 *   <li>Homens (sexo masculino): intervalo minimo de 60 dias entre doacoes;</li>
 *   <li>Mulheres (sexo feminino): intervalo minimo de 90 dias entre doacoes;</li>
 *   <li>Primeira doacao ({@code dataUltimaDoacao == null}): apto imediatamente.</li>
 * </ul>
 *
 * POLIMORFISMO: implementa a interface {@link RegraAptidao}.
 *
 * Responsavel: Guilherme.
 */
public class RegraIntervalo implements RegraAptidao {

    /** Intervalo minimo em dias para doadores do sexo masculino. */
    public static final int DIAS_HOMEM = 60;

    /** Intervalo minimo em dias para doadoras do sexo feminino. */
    public static final int DIAS_MULHER = 90;

    /**
     * Avalia se ja decorreu o intervalo minimo desde a ultima doacao do voluntario.
     *
     * @param doador doador a ser avaliado
     * @param hoje   data de referencia
     * @return resultado apto ou inapto (informando a data exata de liberacao)
     */
    @Override
    public ResultadoAptidao avaliar(Doador doador, LocalDate hoje) {
        LocalDate ultimaDoacao = doador.getDataUltimaDoacao();
        if (ultimaDoacao == null) {
            return ResultadoAptidao.apto();
        }

        int intervalo = switch (doador.getSexo()) {
            case MASCULINO -> DIAS_HOMEM;
            case FEMININO -> DIAS_MULHER;
        };

        LocalDate liberacao = ultimaDoacao.plusDays(intervalo);
        if (hoje.isBefore(liberacao)) {
            long dias = ChronoUnit.DAYS.between(ultimaDoacao, hoje);
            String motivo = "Intervalo mínimo de " + intervalo
                    + " dias desde a última doação (" + dias + " dias passados)";
            return ResultadoAptidao.inapto(motivo, liberacao);
        }

        return ResultadoAptidao.apto();
    }
}
