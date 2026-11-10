package com.doamais.dao;

import com.doamais.exception.DAOException;
import com.doamais.model.Receptor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Acesso a tabela receptores (campanhas). HERANCA: estende BaseDAO&lt;Receptor&gt;.
 *
 * Colunas: id, nome_paciente, hospital, historia, tipo_sanguineo, meta_doacoes,
 * doacoes_recebidas, status ('ATIVA'/'CONCLUIDA'), data_criacao.
 *
 * Responsavel: Ricardo.
 * CONTRATO: listarAtivas e usado pela Maria (home); buscarPorId e
 * atualizarProgresso sao usados pelo Amon (DoacaoService).
 */
public class ReceptorDAO extends BaseDAO<Receptor> {

    @Override
    protected String getTabela() {
        return "receptores";
    }

    @Override
    protected Receptor mapear(ResultSet rs) throws SQLException {
        // TODO(Ricardo): ler as colunas e chamar o construtor completo de Receptor
        throw new UnsupportedOperationException("TODO(Ricardo): ReceptorDAO.mapear");
    }

    @Override
    public void inserir(Receptor r) throws DAOException {
        // TODO(Ricardo): usar inserirRetornandoId(...) e depois r.setId(id)
        throw new UnsupportedOperationException("TODO(Ricardo): ReceptorDAO.inserir");
    }

    @Override
    public void atualizar(Receptor r) throws DAOException {
        // TODO(Ricardo)
        throw new UnsupportedOperationException("TODO(Ricardo): ReceptorDAO.atualizar");
    }

    /**
     * @return campanhas com status ATIVA, das mais urgentes (menor progresso)
     *         para as menos urgentes
     */
    public List<Receptor> listarAtivas() throws DAOException {
        // TODO(Ricardo)
        throw new UnsupportedOperationException("TODO(Ricardo): ReceptorDAO.listarAtivas");
    }

    /**
     * Grava doacoes_recebidas e status do receptor (apos registrarDoacao()).
     */
    public void atualizarProgresso(Receptor r) throws DAOException {
        // TODO(Ricardo)
        throw new UnsupportedOperationException("TODO(Ricardo): ReceptorDAO.atualizarProgresso");
    }
}
