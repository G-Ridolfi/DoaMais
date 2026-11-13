package com.doamais.dao;

import com.doamais.exception.DAOException;
import com.doamais.model.Doacao;
import com.doamais.model.TipoSanguineo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Acesso a tabela doacoes. HERANCA: estende BaseDAO&lt;Doacao&gt;.
 *
 * Colunas: id, doador_id (FK doadores), receptor_id (FK receptores, NULL =
 * estoque geral), data_doacao, local.
 *
 * Responsavel: Amon.
 */
public class DoacaoDAO extends BaseDAO<Doacao> {

    @Override
    protected String getTabela() {
        return "doacoes";
    }

    /**
     * Dica: monte o Doador/Receptor buscando pelos ids com DoadorDAO e
     * ReceptorDAO, ou faca um SELECT com JOIN.
     */
    @Override
    protected Doacao mapear(ResultSet rs) throws SQLException {
        // TODO(Amon)
        throw new UnsupportedOperationException("TODO(Amon): DoacaoDAO.mapear");
    }

    @Override
    public void inserir(Doacao d) throws DAOException {
        // TODO(Amon): receptor_id = null quando a doacao for para o estoque geral
        throw new UnsupportedOperationException("TODO(Amon): DoacaoDAO.inserir");
    }

    @Override
    public void atualizar(Doacao d) throws DAOException {
        // TODO(Amon)
        throw new UnsupportedOperationException("TODO(Amon): DoacaoDAO.atualizar");
    }

    /** @return doacoes do doador, mais recentes primeiro */
    public List<Doacao> listarPorDoador(int doadorId) throws DAOException {
        // TODO(Amon)
        throw new UnsupportedOperationException("TODO(Amon): DoacaoDAO.listarPorDoador");
    }

    /** @return doacoes para a campanha, mais recentes primeiro */
    public List<Doacao> listarPorReceptor(int receptorId) throws DAOException {
        // TODO(Amon)
        throw new UnsupportedOperationException("TODO(Amon): DoacaoDAO.listarPorReceptor");
    }

    /**
     * @return quantidade de doacoes por tipo sanguineo do DOADOR.
     *         Use EnumMap e inclua todos os 8 tipos (com 0 quando nao houver).
     */
    public Map<TipoSanguineo, Integer> contarPorTipo() throws DAOException {
        // TODO(Amon)
        throw new UnsupportedOperationException("TODO(Amon): DoacaoDAO.contarPorTipo");
    }

    /** @return as ultimas 'limite' doacoes, mais recentes primeiro */
    public List<Doacao> listarRecentes(int limite) throws DAOException {
        // TODO(Amon)
        throw new UnsupportedOperationException("TODO(Amon): DoacaoDAO.listarRecentes");
    }
}
