package com.doamais.model;

/**
 * Situacao de uma campanha de reposicao.
 * No banco e gravado pelo nome da constante ("ATIVA" / "CONCLUIDA").
 *
 * Responsavel: Ricardo.
 */
public enum StatusCampanha {

    ATIVA("Ativa"),
    CONCLUIDA("Concluída");

    private final String descricao;

    StatusCampanha(String descricao) {
        this.descricao = descricao;
    }

    /** @return texto para exibir na tela */
    public String getDescricao() {
        return descricao;
    }
}
