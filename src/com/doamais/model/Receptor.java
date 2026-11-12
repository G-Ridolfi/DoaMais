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
        if (metaDoacoes <= 0) return 0;
        return Math.min(100.0, doacoesRecebidas * 100.0 / metaDoacoes);
    }

    /** @return quantas bolsas ainda faltam (nunca negativo) */
    public int getFaltam() {
        return Math.max(0, metaDoacoes - doacoesRecebidas);
    }

    /** @return true se status == CONCLUIDA */
    public boolean isConcluida() {
        return status == StatusCampanha.CONCLUIDA;
    }

    /**
     * Soma 1 doacao recebida. Ao atingir a meta, muda o status para CONCLUIDA.
     *
     * ENCAPSULAMENTO: unica forma de alterar doacoesRecebidas e status.
     * A regra da meta fica protegida aqui — nao existe setter para esses campos.
     *
     * @throws ValidacaoException se a campanha ja estiver concluida
     */
    public void registrarDoacao() throws ValidacaoException {
        if (isConcluida()) {
            throw new ValidacaoException("A campanha de " + nome + " já atingiu a meta.");
        }
        doacoesRecebidas++;
        if (doacoesRecebidas >= metaDoacoes) {
            status = StatusCampanha.CONCLUIDA;
        }
    }

    /**
     * POLIMORFISMO: cada subclasse de Pessoa retorna sua propria descricao.
     * Ex: "Maria Clara – Hospital Estadual Infantil – precisa de O+"
     */
    @Override
    public String getDescricao() {
        String tipo = (tipoSanguineo != null) ? tipoSanguineo.getRotulo() : "";
        return nome + " – " + hospital + " – precisa de " + tipo;
    }

    /**
     * Valida os dados da campanha antes de salvar.
     * Regras: nome e tipo obrigatorios (herdado), hospital obrigatorio,
     * metaDoacoes entre 1 e 50, doacoesRecebidas entre 0 e a meta.
     */
    @Override
    public void validar() throws ValidacaoException {
        validarDadosBasicos();
        if (hospital == null || hospital.isBlank()) {
            throw new ValidacaoException("O hospital é obrigatório.");
        }
        if (metaDoacoes < 1 || metaDoacoes > 50) {
            throw new ValidacaoException("A meta de doações deve ser entre 1 e 50.");
        }
        if (doacoesRecebidas < 0 || doacoesRecebidas > metaDoacoes) {
            throw new ValidacaoException("As doações recebidas devem ser entre 0 e a meta.");
        }
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
