package com.doamais.exception;

/**
 * Dado invalido informado pelo usuario ou recebido de outra camada
 * (ex: nome vazio, peso negativo, tipo sanguineo inexistente, CPF repetido,
 * campo de formulario mal preenchido).
 *
 * Lancada pelos metodos validar() dos modelos, pelos enums ao converter
 * textos e pelo FormUtil ao converter campos de formulario.
 *
 * Responsavel: Gabriel.
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
