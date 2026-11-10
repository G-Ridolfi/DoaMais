package com.doamais.dao;

import com.doamais.exception.DAOException;
import com.doamais.model.Doador;
import com.doamais.model.TipoSanguineo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Acesso a tabela doadores. HERANCA: estende BaseDAO&lt;Doador&gt;.
 *
 * Colunas: id, nome, cpf (UNIQUE), data_nascimento, sexo ('M'/'F'), peso,
 * tipo_sanguineo ('A+'...), telefone, email, data_ultima_doacao (pode ser NULL).
 *
 * Responsavel: Pedro.
 * CONTRATO: buscarPorId e atualizarUltimaDoacao sao usados pelo Amon.
 */
public class DoadorDAO extends BaseDAO<Doador> {

    @Override
    protected String getTabela() {
        return "doadores";
    }

    @Override
    protected Doador mapear(ResultSet rs) throws SQLException {
        // TODO(Pedro): ler as colunas e chamar o construtor completo de Doador
        // (TipoSanguineo.deRotulo / Sexo.deCodigo lancam ValidacaoException: trate e
        //  converta em SQLException("Dado invalido no banco", e) ou similar)
        throw new UnsupportedOperationException("TODO(Pedro): DoadorDAO.mapear");
    }

    /**
     * @throws DAOException se o CPF ja estiver cadastrado (mensagem amigavel)
     */
    @Override
    public void inserir(Doador d) throws DAOException {
        // TODO(Pedro): usar inserirRetornandoId(...) e depois d.setId(id)
        throw new UnsupportedOperationException("TODO(Pedro): DoadorDAO.inserir");
    }

    @Override
    public void atualizar(Doador d) throws DAOException {
        // TODO(Pedro)
        throw new UnsupportedOperationException("TODO(Pedro): DoadorDAO.atualizar");
    }

    /** @return doador com o CPF (11 digitos), ou Optional.empty() */
    public Optional<Doador> buscarPorCpf(String cpf) throws DAOException {
        // TODO(Pedro)
        throw new UnsupportedOperationException("TODO(Pedro): DoadorDAO.buscarPorCpf");
    }

    /** @return doadores do tipo informado, ordenados por nome */
    public List<Doador> listarPorTipo(TipoSanguineo tipo) throws DAOException {
        // TODO(Pedro)
        throw new UnsupportedOperationException("TODO(Pedro): DoadorDAO.listarPorTipo");
    }

    /**
     * Atualiza apenas a data da ultima doacao. Chamado pelo DoacaoService (Amon)
     * logo apos registrar uma doacao.
     */
    public void atualizarUltimaDoacao(int doadorId, LocalDate data) throws DAOException {
        // TODO(Pedro)
        throw new UnsupportedOperationException("TODO(Pedro): DoadorDAO.atualizarUltimaDoacao");
    }
}
