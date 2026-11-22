package com.doamais.exception;

/**
 * HERANCA: erro de validacao de dados (estende DoaMaisException).
 */
public class ValidacaoException extends DoaMaisException {

    private static final long serialVersionUID = 1L;

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }

    public ValidacaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
