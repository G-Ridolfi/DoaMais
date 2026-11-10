package com.doamais.service;

import java.time.LocalDate;
import java.util.List;

/**
 * Resultado da avaliacao de aptidao (de uma regra ou do motor inteiro).
 *
 * Objeto IMUTAVEL: depois de criado nao muda.
 *
 * Responsavel: Guilherme.
 * CONTRATO: usado pelo AptidaoHandler e pelo MotorAptidao.
 */
public class ResultadoAptidao {

    private final boolean apto;
    private final List<String> motivos;      // vazio quando apto
    private final LocalDate dataLiberacao;   // null quando apto ou sem previsao

    public ResultadoAptidao(boolean apto, List<String> motivos, LocalDate dataLiberacao) {
        this.apto = apto;
        this.motivos = List.copyOf(motivos);
        this.dataLiberacao = dataLiberacao;
    }

    /** Fabrica: resultado apto, sem motivos. */
    public static ResultadoAptidao apto() {
        return new ResultadoAptidao(true, List.of(), null);
    }

    /**
     * Fabrica: resultado inapto com um motivo.
     *
     * @param dataLiberacao data a partir da qual podera doar; null se nao houver previsao
     */
    public static ResultadoAptidao inapto(String motivo, LocalDate dataLiberacao) {
        return new ResultadoAptidao(false, List.of(motivo), dataLiberacao);
    }

    public boolean isApto() {
        return apto;
    }

    /** @return lista somente-leitura de motivos (vazia quando apto) */
    public List<String> getMotivos() {
        return motivos;
    }

    public LocalDate getDataLiberacao() {
        return dataLiberacao;
    }
}
