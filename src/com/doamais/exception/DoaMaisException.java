package com.doamais.exception;

/**
 * Excecao base de todo o sistema DoaMais.
 *
 * Todas as excecoes proprias do projeto herdam desta classe (HERANCA).
 * Assim, o BaseHandler pode capturar qualquer erro de negocio com um unico
 * {@code catch (DoaMaisException e)} e mostrar a mensagem ao usuario
 * (POLIMORFISMO: cada subclasse traz sua propria mensagem).
 *
 * E uma excecao verificada (checked): o compilador obriga quem chama
 * a tratar (try/catch) ou repassar (throws) o erro.
 *
 * Responsavel: Gabriel.
 */
public class DoaMaisException extends Exception {

    private static final long serialVersionUID = 1L;

    /** @param mensagem texto amigavel, pode ser exibido diretamente na tela */
    public DoaMaisException(String mensagem) {
        super(mensagem);
    }

    /**
     * @param mensagem texto amigavel, pode ser exibido diretamente na tela
     * @param causa    excecao original (ex: SQLException), mantida para depuracao
     */
    public DoaMaisException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
