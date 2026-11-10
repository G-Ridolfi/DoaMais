package com.doamais.service;

import com.doamais.model.Doador;

import java.time.LocalDate;

/**
 * INTERFACE de uma regra de aptidao para doacao de sangue.
 *
 * POLIMORFISMO: RegraIdade, RegraPeso e RegraIntervalo implementam esta
 * interface. O MotorAptidao guarda uma List&lt;RegraAptidao&gt; e chama
 * avaliar() em cada uma sem saber qual regra e. Para criar uma nova
 * regra, basta criar outra classe, sem mexer no motor.
 *
 * Responsavel: Guilherme.
 * CONTRATO: nao alterar a assinatura sem avisar o grupo.
 */
public interface RegraAptidao {

    /**
     * Avalia o doador nesta regra.
     *
     * @param doador doador a ser avaliado (pode ainda nao estar salvo no banco)
     * @param hoje   data de referencia (permite testar com datas fixas)
     * @return ResultadoAptidao.apto() ou ResultadoAptidao.inapto(motivo, dataLiberacao)
     */
    ResultadoAptidao avaliar(Doador doador, LocalDate hoje);
}
