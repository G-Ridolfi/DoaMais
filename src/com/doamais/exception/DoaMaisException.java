package com.doamais.exception;

/**
 * HERANCA: excecao base verificada (checked) do sistema.
 * Permite ao BaseHandler capturar qualquer erro de negocio em um unico catch (DoaMaisException).
 */
public class DoaMaisException extends Exception {

    private static final long serialVersionUID = 1L;

    public DoaMaisException(String mensagem) {
        super(mensagem);
    }

    public DoaMaisException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
