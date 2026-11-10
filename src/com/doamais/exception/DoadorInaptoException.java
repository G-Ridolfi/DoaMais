package com.doamais.exception;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * Lancada quando o doador NAO pode doar hoje segundo as regras de aptidao
 * do Ministerio da Saude (idade, peso, intervalo entre doacoes).
 *
 * Diferente das outras excecoes, carrega dados extras: a lista de motivos
 * e a data a partir da qual o doador estara liberado (pode ser nula, ex:
 * peso insuficiente nao tem data de liberacao).
 *
 * Lancada por MotorAptidao.verificarOuLancar() e tratada no fluxo de
 * registro de doacao (DoacaoService) e na tela (BaseHandler).
 *
 * Responsavel: Guilherme.
 */
public class DoadorInaptoException extends DoaMaisException {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final List<String> motivos;
    private final LocalDate dataLiberacao;

    /**
     * @param motivos       motivos de inaptidao (nao vazio)
     * @param dataLiberacao data em que podera doar; null se nao houver previsao
     */
    public DoadorInaptoException(List<String> motivos, LocalDate dataLiberacao) {
        super(montarMensagem(motivos, dataLiberacao));
        this.motivos = List.copyOf(motivos);
        this.dataLiberacao = dataLiberacao;
    }

    private static String montarMensagem(List<String> motivos, LocalDate dataLiberacao) {
        String msg = "Doador inapto: " + String.join("; ", motivos) + ".";
        if (dataLiberacao != null) {
            msg += " Liberado para doar a partir de " + dataLiberacao.format(BR) + ".";
        }
        return msg;
    }

    /** @return lista somente-leitura com os motivos de inaptidao */
    public List<String> getMotivos() {
        return Collections.unmodifiableList(motivos);
    }

    /** @return data de liberacao, ou null se nao houver previsao */
    public LocalDate getDataLiberacao() {
        return dataLiberacao;
    }
}
