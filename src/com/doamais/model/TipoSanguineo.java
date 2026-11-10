package com.doamais.model;

import com.doamais.exception.ValidacaoException;

/**
 * Os 8 tipos sanguineos do sistema ABO/Rh.
 *
 * No Java use sempre o enum (nunca String solta). No banco e nos
 * formularios o valor e gravado pelo rotulo ("A+", "O-", ...).
 *
 * Responsavel: Pedro.
 */
public enum TipoSanguineo {

    A_POS("A+"), A_NEG("A-"),
    B_POS("B+"), B_NEG("B-"),
    AB_POS("AB+"), AB_NEG("AB-"),
    O_POS("O+"), O_NEG("O-");

    private final String rotulo;

    TipoSanguineo(String rotulo) {
        this.rotulo = rotulo;
    }

    /** @return rotulo legivel, ex: "O-". E o valor gravado no banco. */
    public String getRotulo() {
        return rotulo;
    }

    /**
     * Converte o rotulo ("A+", "o-", " AB+ ") no enum correspondente.
     *
     * @throws ValidacaoException se o texto nao for um tipo sanguineo valido
     */
    public static TipoSanguineo deRotulo(String rotulo) throws ValidacaoException {
        if (rotulo != null) {
            String r = rotulo.trim().toUpperCase();
            for (TipoSanguineo t : values()) {
                if (t.rotulo.equals(r)) {
                    return t;
                }
            }
        }
        throw new ValidacaoException("Tipo sanguíneo inválido: " + rotulo);
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
