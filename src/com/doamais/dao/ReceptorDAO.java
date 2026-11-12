package com.doamais.dao;

import com.doamais.exception.DAOException;
import com.doamais.exception.ValidacaoException;
import com.doamais.model.Receptor;
import com.doamais.model.StatusCampanha;
import com.doamais.model.TipoSanguineo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
        int id = rs.getInt("id");
        String nomePaciente = rs.getString("nome_paciente");
        String hospital = rs.getString("hospital");
        String historia = rs.getString("historia");
        int metaDoacoes = rs.getInt("meta_doacoes");
        int doacoesRecebidas = rs.getInt("doacoes_recebidas");
        StatusCampanha status = StatusCampanha.valueOf(rs.getString("status"));
        LocalDate dataCriacao = LocalDate.parse(rs.getString("data_criacao"));

        TipoSanguineo tipoSanguineo;
        try {
            tipoSanguineo = TipoSanguineo.deRotulo(rs.getString("tipo_sanguineo"));
        } catch (ValidacaoException e) {
            throw new SQLException("Dado inválido no banco: " + e.getMessage(), e);
        }

        return new Receptor(id, nomePaciente, tipoSanguineo, hospital, historia,
                metaDoacoes, doacoesRecebidas, status, dataCriacao);
    }

    @Override
    public void inserir(Receptor r) throws DAOException {
        String sql = "INSERT INTO receptores (nome_paciente, hospital, historia, tipo_sanguineo, "
                + "meta_doacoes, doacoes_recebidas, status, data_criacao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        int id = inserirRetornandoId(sql,
                r.getNome(),
                r.getHospital(),
                r.getHistoria(),
                r.getTipoSanguineo() != null ? r.getTipoSanguineo().getRotulo() : null,
                r.getMetaDoacoes(),
                r.getDoacoesRecebidas(),
                r.getStatus().name(),
                r.getDataCriacao() != null ? r.getDataCriacao().toString() : null
        );
        r.setId(id);
    }

    @Override
    public void atualizar(Receptor r) throws DAOException {
        String sql = "UPDATE receptores SET nome_paciente = ?, hospital = ?, historia = ?, "
                + "tipo_sanguineo = ?, meta_doacoes = ?, doacoes_recebidas = ?, status = ?, "
                + "data_criacao = ? WHERE id = ?";
        int linhas = executar(sql,
                r.getNome(),
                r.getHospital(),
                r.getHistoria(),
                r.getTipoSanguineo() != null ? r.getTipoSanguineo().getRotulo() : null,
                r.getMetaDoacoes(),
                r.getDoacoesRecebidas(),
                r.getStatus().name(),
                r.getDataCriacao() != null ? r.getDataCriacao().toString() : null,
                r.getId()
        );
        if (linhas == 0) {
            throw new DAOException("Campanha não encontrada");
        }
    }

    /**
     * @return campanhas com status ATIVA, das mais urgentes (menor progresso)
     *         para as menos urgentes
     */
    public List<Receptor> listarAtivas() throws DAOException {
        return consultar(
                "SELECT * FROM receptores WHERE status = 'ATIVA' "
                + "ORDER BY (CAST(doacoes_recebidas AS REAL) / meta_doacoes) ASC, data_criacao ASC"
        );
    }

    /**
     * Grava doacoes_recebidas e status do receptor (apos registrarDoacao()).
     */
    public void atualizarProgresso(Receptor r) throws DAOException {
        executar("UPDATE receptores SET doacoes_recebidas = ?, status = ? WHERE id = ?",
                r.getDoacoesRecebidas(),
                r.getStatus().name(),
                r.getId()
        );
    }
}
