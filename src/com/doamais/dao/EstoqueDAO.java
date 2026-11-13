package com.doamais.dao;

import com.doamais.db.Database;
import com.doamais.exception.DAOException;
import com.doamais.model.NivelEstoque;
import com.doamais.model.TipoSanguineo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;

/**
 * Acesso a tabela estoque_hemoes (termometro do HEMOES na home).
 *
 * NAO estende BaseDAO porque a chave da tabela e o tipo sanguineo, nao um id.
 * Usa Database.getConnection() + PreparedStatement + try-with-resources
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
        // COLECAO (EnumMap): comeca com os 8 tipos em NORMAL; tipo ausente no
        // banco continua NORMAL.
        EnumMap<TipoSanguineo, NivelEstoque> termometro = new EnumMap<>(TipoSanguineo.class);
        for (TipoSanguineo tipo : TipoSanguineo.values()) {
            termometro.put(tipo, NivelEstoque.NORMAL);
        }

        String sql = "SELECT tipo_sanguineo, nivel FROM estoque_hemoes";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TipoSanguineo tipo = TipoSanguineo.deRotulo(rs.getString("tipo_sanguineo"));
                NivelEstoque nivel = NivelEstoque.valueOf(rs.getString("nivel"));
                termometro.put(tipo, nivel);
            }
        } catch (SQLException e) {
            // TRATAMENTO DE EXCECAO: SQLException vira DAOException
            throw new DAOException("Erro ao carregar o termômetro do estoque", e);
        } catch (Exception e) {
            // tipo ou nivel invalido gravado no banco (ValidacaoException / IllegalArgumentException)
            throw new DAOException("Dado inválido na tabela estoque_hemoes: " + e.getMessage(), e);
        }
        return termometro;
    }

    /** Atualiza o nivel de um tipo e a data atualizado_em. */
    public void atualizarNivel(TipoSanguineo tipo, NivelEstoque nivel) throws DAOException {
        String sql = "UPDATE estoque_hemoes SET nivel = ?, atualizado_em = datetime('now') "
                + "WHERE tipo_sanguineo = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nivel.name());
            ps.setString(2, tipo.getRotulo());
            if (ps.executeUpdate() == 0) {
                throw new DAOException("Tipo sanguíneo não encontrado no estoque: " + tipo.getRotulo());
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar o nível do estoque", e);
        }
    }
}