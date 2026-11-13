package com.doamais.dao;

import com.doamais.db.Database;
import com.doamais.exception.DAOException;
import com.doamais.exception.ValidacaoException;
import com.doamais.model.Doacao;
import com.doamais.model.Doador;
import com.doamais.model.Receptor;
import com.doamais.model.TipoSanguineo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Acesso a tabela doacoes. HERANCA: estende BaseDAO&lt;Doacao&gt;.
 *
 * Colunas: id, doador_id (FK doadores), receptor_id (FK receptores, NULL =
 * estoque geral), data_doacao, local.
 *
 * Este DAO reaproveita DoadorDAO e ReceptorDAO (COMPOSICAO) para montar a
 * Doacao com os objetos completos de Doador e Receptor.
 *
 * Responsavel: Amon.
 */
public class DoacaoDAO extends BaseDAO<Doacao> {

    private final DoadorDAO doadorDAO = new DoadorDAO();
    private final ReceptorDAO receptorDAO = new ReceptorDAO();

    @Override
    protected String getTabela() {
        return "doacoes";
    }

    /**
     * POLIMORFISMO: o listarTodos()/buscarPorId() da BaseDAO chamam este metodo.
     * Monta a Doacao com o Doador e o Receptor completos, buscando-os pelos ids.
     * Como buscarPorId lanca DAOException e mapear so pode lancar SQLException,
     * a DAOException e convertida (o BaseDAO a reconverte em DAOException).
     */
    @Override
    protected Doacao mapear(ResultSet rs) throws SQLException {
        // le tudo do ResultSet antes de consultar outros DAOs
        int id = rs.getInt("id");
        int doadorId = rs.getInt("doador_id");
        boolean semReceptor = rs.getObject("receptor_id") == null;
        int receptorId = semReceptor ? 0 : rs.getInt("receptor_id");
        String dataStr = rs.getString("data_doacao");
        String local = rs.getString("local");

        LocalDate dataDoacao = (dataStr != null && !dataStr.isBlank())
                ? LocalDate.parse(dataStr)
                : null;

        try {
            Doador doador = doadorDAO.buscarPorId(doadorId)
                    .orElseThrow(() -> new DAOException("Doador " + doadorId + " não encontrado"));

            Receptor receptor = null; // null = estoque geral
            if (!semReceptor) {
                receptor = receptorDAO.buscarPorId(receptorId)
                        .orElseThrow(() -> new DAOException("Campanha " + receptorId + " não encontrada"));
            }
            return new Doacao(id, doador, receptor, dataDoacao, local);
        } catch (DAOException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    @Override
    public void inserir(Doacao d) throws DAOException {
        String sql = "INSERT INTO doacoes (doador_id, receptor_id, data_doacao, local) VALUES (?, ?, ?, ?)";
        int id = inserirRetornandoId(sql,
                d.getDoador().getId(),
                receptorIdOuNull(d),   // null quando for estoque geral
                d.getDataDoacao() != null ? d.getDataDoacao().toString() : null,
                d.getLocal()
        );
        d.setId(id);
    }

    @Override
    public void atualizar(Doacao d) throws DAOException {
        String sql = "UPDATE doacoes SET doador_id = ?, receptor_id = ?, data_doacao = ?, local = ? WHERE id = ?";
        int linhas = executar(sql,
                d.getDoador().getId(),
                receptorIdOuNull(d),
                d.getDataDoacao() != null ? d.getDataDoacao().toString() : null,
                d.getLocal(),
                d.getId()
        );
        if (linhas == 0) {
            throw new DAOException("Doação não encontrada");
        }
    }

    /** @return doacoes do doador, mais recentes primeiro */
    public List<Doacao> listarPorDoador(int doadorId) throws DAOException {
        return consultar("SELECT * FROM doacoes WHERE doador_id = ? "
                + "ORDER BY data_doacao DESC, id DESC", doadorId);
    }

    /** @return doacoes para a campanha, mais recentes primeiro */
    public List<Doacao> listarPorReceptor(int receptorId) throws DAOException {
        return consultar("SELECT * FROM doacoes WHERE receptor_id = ? "
                + "ORDER BY data_doacao DESC, id DESC", receptorId);
    }

    /**
     * @return quantidade de doacoes por tipo sanguineo do DOADOR, com os 8 tipos
     *         (0 quando nao houver doacoes).
     *
     * COLECAO (EnumMap): foi escolhido em vez de HashMap porque a chave e um
     * enum. O EnumMap guarda os dados internamente em um array indexado pela
     * posicao do enum, entao e mais rapido e usa menos memoria, mantem as
     * chaves sempre na ordem de declaracao do enum (A+, A-, B+...) e so aceita
     * chaves do tipo TipoSanguineo (chave tipada).
     */
    public Map<TipoSanguineo, Integer> contarPorTipo() throws DAOException {
        Map<TipoSanguineo, Integer> contagem = new EnumMap<>(TipoSanguineo.class);
        for (TipoSanguineo tipo : TipoSanguineo.values()) {
            contagem.put(tipo, 0); // garante os 8 tipos, mesmo sem doacoes
        }

        // Este SELECT nao devolve Doacao, entao nao usa consultar()/mapear()
        String sql = "SELECT d.tipo_sanguineo, COUNT(*) AS total "
                + "FROM doacoes x JOIN doadores d ON d.id = x.doador_id "
                + "GROUP BY d.tipo_sanguineo";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TipoSanguineo tipo = TipoSanguineo.deRotulo(rs.getString("tipo_sanguineo"));
                contagem.put(tipo, rs.getInt("total"));
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao contar doações por tipo sanguíneo", e);
        } catch (ValidacaoException e) {
            throw new DAOException("Tipo sanguíneo inválido no banco: " + e.getMessage(), e);
        }
        return contagem;
    }

    /** @return as ultimas 'limite' doacoes, mais recentes primeiro */
    public List<Doacao> listarRecentes(int limite) throws DAOException {
        return consultar("SELECT * FROM doacoes ORDER BY data_doacao DESC, id DESC LIMIT ?", limite);
    }

    /** @return id do receptor da doacao, ou null se for estoque geral */
    private Integer receptorIdOuNull(Doacao d) {
        return d.isDirecionada() ? d.getReceptor().getId() : null;
    }
}
