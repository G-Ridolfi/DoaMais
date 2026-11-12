package com.doamais.dao;

import com.doamais.exception.DAOException;
import com.doamais.exception.ValidacaoException;
import com.doamais.model.Doador;
import com.doamais.model.Sexo;
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
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String cpf = rs.getString("cpf");
        LocalDate dataNascimento = LocalDate.parse(rs.getString("data_nascimento"));

        Sexo sexo;
        TipoSanguineo tipoSanguineo;
        try {
            sexo = Sexo.deCodigo(rs.getString("sexo"));
            tipoSanguineo = TipoSanguineo.deRotulo(rs.getString("tipo_sanguineo"));
        } catch (ValidacaoException e) {
            throw new SQLException("Dado inválido no banco: " + e.getMessage(), e);
        }

        double peso = rs.getDouble("peso");
        String telefone = rs.getString("telefone");
        String email = rs.getString("email");

        String dataUltimaStr = rs.getString("data_ultima_doacao");
        LocalDate dataUltimaDoacao = (dataUltimaStr != null && !dataUltimaStr.isBlank())
                ? LocalDate.parse(dataUltimaStr)
                : null;

        return new Doador(id, nome, tipoSanguineo, cpf, dataNascimento, sexo, peso, telefone, email, dataUltimaDoacao);
    }

    /**
     * @throws DAOException se o CPF ja estiver cadastrado (mensagem amigavel)
     */
    @Override
    public void inserir(Doador d) throws DAOException {
        if (d.getCpf() != null && buscarPorCpf(d.getCpf()).isPresent()) {
            throw new DAOException("Já existe um doador cadastrado com o CPF " + d.getCpf());
        }

        String sql = "INSERT INTO doadores (nome, cpf, data_nascimento, sexo, peso, tipo_sanguineo, telefone, email, data_ultima_doacao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            int id = inserirRetornandoId(sql,
                    d.getNome(),
                    d.getCpf(),
                    d.getDataNascimento() != null ? d.getDataNascimento().toString() : null,
                    d.getSexo() != null ? d.getSexo().getCodigo() : null,
                    d.getPeso(),
                    d.getTipoSanguineo() != null ? d.getTipoSanguineo().getRotulo() : null,
                    d.getTelefone(),
                    d.getEmail(),
                    d.getDataUltimaDoacao() != null ? d.getDataUltimaDoacao().toString() : null
            );
            d.setId(id);
        } catch (DAOException e) {
            if (e.getMessage() != null && (e.getMessage().contains("duplicado") || e.getMessage().contains("UNIQUE"))) {
                throw new DAOException("Já existe um doador cadastrado com o CPF " + d.getCpf(), e);
            }
            throw e;
        }
    }

    @Override
    public void atualizar(Doador d) throws DAOException {
        String sql = "UPDATE doadores SET nome = ?, cpf = ?, data_nascimento = ?, sexo = ?, peso = ?, "
                + "tipo_sanguineo = ?, telefone = ?, email = ?, data_ultima_doacao = ? WHERE id = ?";
        try {
            int linhas = executar(sql,
                    d.getNome(),
                    d.getCpf(),
                    d.getDataNascimento() != null ? d.getDataNascimento().toString() : null,
                    d.getSexo() != null ? d.getSexo().getCodigo() : null,
                    d.getPeso(),
                    d.getTipoSanguineo() != null ? d.getTipoSanguineo().getRotulo() : null,
                    d.getTelefone(),
                    d.getEmail(),
                    d.getDataUltimaDoacao() != null ? d.getDataUltimaDoacao().toString() : null,
                    d.getId()
            );
            if (linhas == 0) {
                throw new DAOException("Doador não encontrado");
            }
        } catch (DAOException e) {
            if (e.getMessage() != null && (e.getMessage().contains("duplicado") || e.getMessage().contains("UNIQUE"))) {
                throw new DAOException("Já existe um doador cadastrado com o CPF " + d.getCpf(), e);
            }
            throw e;
        }
    }

    /** @return doador com o CPF (11 digitos), ou Optional.empty() */
    public Optional<Doador> buscarPorCpf(String cpf) throws DAOException {
        String cpfLimpo = (cpf != null) ? cpf.replaceAll("\\D", "") : "";
        List<Doador> lista = consultar("SELECT * FROM doadores WHERE cpf = ?", cpfLimpo);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    /** @return doadores do tipo informado, ordenados por nome */
    public List<Doador> listarPorTipo(TipoSanguineo tipo) throws DAOException {
        if (tipo == null) {
            return listarTodos();
        }
        return consultar("SELECT * FROM doadores WHERE tipo_sanguineo = ? ORDER BY nome", tipo.getRotulo());
    }

    /**
     * Atualiza apenas a data da ultima doacao. Chamado pelo DoacaoService (Amon)
     * logo apos registrar uma doacao.
     */
    public void atualizarUltimaDoacao(int doadorId, LocalDate data) throws DAOException {
        String dataStr = (data != null) ? data.toString() : null;
        int linhas = executar("UPDATE doadores SET data_ultima_doacao = ? WHERE id = ?", dataStr, doadorId);
        if (linhas == 0) {
            throw new DAOException("Doador não encontrado");
        }
    }
}
