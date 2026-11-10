package com.doamais.dao;

import com.doamais.exception.DAOException;
import com.doamais.model.NivelEstoque;
import com.doamais.model.TipoSanguineo;

import java.util.EnumMap;

/**
 * Acesso a tabela estoque_hemoes (termometro do HEMOES na home).
 *
 * NAO estende BaseDAO porque a chave da tabela e o tipo sanguineo, nao um id.
 * Use Database.getConnection() + PreparedStatement + try-with-resources
 * diretamente, convertendo SQLException em DAOException.
 *
 * Colunas: tipo_sanguineo (PK, 'A+'...), nivel ('CRITICO'/'BAIXO'/'NORMAL'/'ALTO'),
 * atualizado_em. O Database ja insere os 8 tipos no primeiro boot.
 *
 * Responsavel: Amon.
 * CONTRATO: carregarTermometro e usado pela Maria (HomeHandler).
 */
public class EstoqueDAO {

    /**
     * @return os 8 tipos sanguineos, na ordem do enum, com o nivel atual.
     *         EnumMap mantem a ordem dos tipos e e mais eficiente que HashMap.
     */
    public EnumMap<TipoSanguineo, NivelEstoque> carregarTermometro() throws DAOException {
        // TODO(Amon)
        throw new UnsupportedOperationException("TODO(Amon): EstoqueDAO.carregarTermometro");
    }

    /** Atualiza o nivel de um tipo e a data atualizado_em. */
    public void atualizarNivel(TipoSanguineo tipo, NivelEstoque nivel) throws DAOException {
        // TODO(Amon)
        throw new UnsupportedOperationException("TODO(Amon): EstoqueDAO.atualizarNivel");
    }
}
