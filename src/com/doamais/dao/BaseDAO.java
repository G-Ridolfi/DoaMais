package com.doamais.dao;

import com.doamais.db.Database;
import com.doamais.exception.DAOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * HERANCA + GENERICS (&lt;T&gt;): superclasse abstrata que DoadorDAO, ReceptorDAO
 * e DoacaoDAO estendem para reaproveitar as operacoes JDBC.
 *
 * @param <T> tipo da entidade manipulada pelo DAO
 */
public abstract class BaseDAO<T> {

    // Metodos ABSTRATOS: cada DAO filho implementa (@Override)
    protected abstract String getTabela();

    protected abstract T mapear(ResultSet rs) throws SQLException;

    public abstract void inserir(T obj) throws DAOException;

    public abstract void atualizar(T obj) throws DAOException;

    /** COLECAO (Optional): evita retornar null quando o id nao existe no banco. */
    public Optional<T> buscarPorId(int id) throws DAOException {
        List<T> resultado = consultar("SELECT * FROM " + getTabela() + " WHERE id = ?", id);
        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.get(0));
    }

    /** POLIMORFISMO: escrito uma vez aqui, usa getTabela() e mapear() de cada DAO filho. */
    public List<T> listarTodos() throws DAOException {
        return consultar("SELECT * FROM " + getTabela() + " ORDER BY id");
    }

    public boolean excluir(int id) throws DAOException {
        return executar("DELETE FROM " + getTabela() + " WHERE id = ?", id) > 0;
    }

    /** Executa SELECT e converte cada linha usando o mapear() do DAO filho. */
    protected List<T> consultar(String sql, Object... parametros) throws DAOException {
        // COLECAO (List / ArrayList): mantem a ordem retornada pelo SELECT
        List<T> lista = new ArrayList<>();
        // try-with-resources: fecha Connection, PreparedStatement e ResultSet automaticamente
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            preencherParametros(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // POLIMORFISMO (Template Method): chama o mapear() da subclasse concreta
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            // EXCECAO: converte SQLException em DAOException (isolamento de camadas)
            throw new DAOException("Erro ao consultar " + getTabela(), e);
        }
        return lista;
    }

    /** Executa INSERT, UPDATE ou DELETE e retorna o total de linhas afetadas. */
    protected int executar(String sql, Object... parametros) throws DAOException {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            preencherParametros(ps, parametros);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw converterErroDeGravacao(e);
        }
    }

    /** Executa INSERT e recupera o id AUTOINCREMENT gerado pelo SQLite. */
    protected int inserirRetornandoId(String sql, Object... parametros) throws DAOException {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherParametros(ps, parametros);
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    return chaves.getInt(1);
                }
            }
            throw new DAOException("Nenhum id foi gerado ao inserir em " + getTabela());
        } catch (SQLException e) {
            throw converterErroDeGravacao(e);
        }
    }

    /** PreparedStatement com "?": evita SQL Injection (nunca concatena valores do usuario). */
    private void preencherParametros(PreparedStatement ps, Object... parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            ps.setObject(i + 1, parametros[i]);
        }
    }

    /** Traduz violacao de UNIQUE (ex: CPF duplicado) em DAOException amigavel. */
    private DAOException converterErroDeGravacao(SQLException e) {
        String mensagem = e.getMessage();
        if (mensagem != null && mensagem.contains("UNIQUE")) {
            return new DAOException("Registro duplicado em " + getTabela(), e);
        }
        return new DAOException("Erro ao gravar em " + getTabela(), e);
    }
}
