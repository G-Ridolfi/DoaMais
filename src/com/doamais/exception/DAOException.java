package com.doamais.exception;

/**
 * Erro de persistencia (banco de dados ou arquivo CSV).
 *
 * Regra do projeto: os DAOs NUNCA deixam uma SQLException "vazar" para as
 * outras camadas. Ela e capturada e convertida em DAOException, mantendo a
 * original como causa:
 * <pre>
 *   } catch (SQLException e) {
 *       throw new DAOException("Erro ao salvar doador", e);
 *   }
 * </pre>
 *
 * Responsavel: Gabriel.
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
