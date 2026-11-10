package com.doamais.model;

/**
 * Nivel do estoque de um tipo sanguineo no HEMOES (termometro da home).
 * No banco e gravado pelo nome da constante ("CRITICO", "BAIXO", ...).
 *
 * Responsavel: Amon.
 */
public enum NivelEstoque {

    CRITICO("Crítico", "#c62828"),
    BAIXO("Baixo", "#ef6c00"),
    NORMAL("Normal", "#2e7d32"),
    ALTO("Alto", "#1565c0");

    private final String descricao;
    private final String cor;

    NivelEstoque(String descricao, String cor) {
        this.descricao = descricao;
        this.cor = cor;
    }

    /** @return texto para exibir na tela, ex: "Crítico" */
    public String getDescricao() {
        return descricao;
    }

    /** @return cor hexadecimal do badge, ex: "#c62828" */
    public String getCor() {
        return cor;
    }
}
