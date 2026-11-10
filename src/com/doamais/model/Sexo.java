package com.doamais.model;

import com.doamais.exception.ValidacaoException;

/**
 * Sexo biologico do doador. Usado pela regra de intervalo entre doacoes
 * (60 dias para homens, 90 para mulheres).
 *
 * No banco e gravado pelo codigo ("M" ou "F").
 *
 * Responsavel: Pedro.
 */
public enum Sexo {

    MASCULINO("M", "Masculino"),
    FEMININO("F", "Feminino");

    private final String codigo;
    private final String descricao;

    Sexo(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    /** @return "M" ou "F" (valor gravado no banco) */
    public String getCodigo() {
        return codigo;
    }

    /** @return "Masculino" ou "Feminino" (para exibir na tela) */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Converte "M"/"F" (maiusculo ou minusculo) no enum.
     *
     * @throws ValidacaoException se o codigo for invalido
     */
    public static Sexo deCodigo(String codigo) throws ValidacaoException {
        if (codigo != null) {
            for (Sexo s : values()) {
                if (s.codigo.equalsIgnoreCase(codigo.trim())) {
                    return s;
                }
            }
        }
        throw new ValidacaoException("Sexo inválido: " + codigo);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
