package com.doamais.model;

import com.doamais.exception.ValidacaoException;

import java.time.LocalDate;

/**
 * Paciente internado que precisa de doacoes de reposicao (uma CAMPANHA).
 * HERANCA: estende Pessoa (o nome herdado e o nome do paciente).
 *
 * ENCAPSULAMENTO: doacoesRecebidas e status NAO tem setter. Eles so mudam
 * pelo metodo registrarDoacao(), que garante a regra da meta.
 *
 * Tabela no banco: receptores.
 *
 * Responsavel: Ricardo.
 * CONTRATO: usado por Amon (doacoes) e Maria Vitoria (feed da home).
 * Os metodos marcados com TODO(Ricardo) devem ser implementados pelo Ricardo.
 */
public class Receptor extends Pessoa {

    private String hospital;
    private String historia;          // relato da familia (opcional)
    private int metaDoacoes;          // quantas bolsas sao necessarias
    private int doacoesRecebidas;
    private StatusCampanha status;
    private LocalDate dataCriacao;

    /** Construtor para uma campanha NOVA (0 doacoes, ATIVA, criada hoje). */
    public Receptor(String nomePaciente, TipoSanguineo tipoSanguineo, String hospital,
                    String historia, int metaDoacoes) {
        this(0, nomePaciente, tipoSanguineo, hospital, historia, metaDoacoes,
                0, StatusCampanha.ATIVA, LocalDate.now());
    }

    /** Construtor completo (usado pelo ReceptorDAO.mapear). */
    public Receptor(int id, String nomePaciente, TipoSanguineo tipoSanguineo, String hospital,
                    String historia, int metaDoacoes, int doacoesRecebidas,
                    StatusCampanha status, LocalDate dataCriacao) {
        super(id, nomePaciente, tipoSanguineo);
        this.hospital = hospital;
        this.historia = historia;
        this.metaDoacoes = metaDoacoes;
        this.doacoesRecebidas = doacoesRecebidas;
        this.status = status;
        this.dataCriacao = dataCriacao;
    }

    /** @return percentual de 0.0 a 100.0 (nunca passa de 100) */
    public double getProgresso() {
        // TODO(Ricardo): doacoesRecebidas * 100.0 / metaDoacoes, limitado a 100
        throw new UnsupportedOperationException("TODO(Ricardo): Receptor.getProgresso");
    }

    /** @return quantas bolsas ainda faltam (nunca negativo) */
    public int getFaltam() {
        // TODO(Ricardo)
        throw new UnsupportedOperationException("TODO(Ricardo): Receptor.getFaltam");
    }

    /** @return true se status == CONCLUIDA */
    public boolean isConcluida() {
        return status == StatusCampanha.CONCLUIDA;
    }

    /**
     * Soma 1 doacao recebida. Ao atingir a meta, muda o status para CONCLUIDA.
     *
     * @throws ValidacaoException se a campanha ja estiver concluida
     */
    public void registrarDoacao() throws ValidacaoException {
        // TODO(Ricardo): implementar a regra descrita no Javadoc
        throw new UnsupportedOperationException("TODO(Ricardo): Receptor.registrarDoacao");
    }

    /** Ex: "Maria Clara – Hospital Estadual Infantil – precisa de O-" */
    @Override
    public String getDescricao() {
        // TODO(Ricardo)
        throw new UnsupportedOperationException("TODO(Ricardo): Receptor.getDescricao");
    }

    /** Regras: validarDadosBasicos(); hospital obrigatorio; metaDoacoes > 0. */
    @Override
    public void validar() throws ValidacaoException {
        // TODO(Ricardo)
        throw new UnsupportedOperationException("TODO(Ricardo): Receptor.validar");
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getHistoria() {
        return historia;
    }

    public void setHistoria(String historia) {
        this.historia = historia;
    }

    public int getMetaDoacoes() {
        return metaDoacoes;
    }

    public void setMetaDoacoes(int metaDoacoes) {
        this.metaDoacoes = metaDoacoes;
    }

    public int getDoacoesRecebidas() {
        return doacoesRecebidas;
    }

    public StatusCampanha getStatus() {
        return status;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }
}
