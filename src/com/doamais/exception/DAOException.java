package com.doamais.exception;

/**
 * HERANCA: erro de persistencia (estende DoaMaisException).
 * Isola o JDBC: os DAOs convertem SQLException em DAOException mantendo a causa original.
 */
public class DAOException extends DoaMaisException {

    private static final long serialVersionUID = 1L;

    public DAOException(String mensagem) {
        super(mensagem);
    }

    public DAOException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
