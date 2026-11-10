package com.doamais.dao;

import com.doamais.exception.DAOException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Classe ABSTRATA e GENERICA que todos os DAOs com chave "id" herdam.
 *
 * HERANCA: DoadorDAO, ReceptorDAO e DoacaoDAO estendem BaseDAO&lt;T&gt;.
 * POLIMORFISMO: listarTodos() e buscarPorId() sao escritos uma unica vez
 * aqui e chamam mapear(), que cada DAO filho implementa do seu jeito.
 *
 * COMO USAR (exemplo em um DAO filho):
 * <pre>
 *   public List&lt;Doador&gt; listarPorTipo(TipoSanguineo t) throws DAOException {
 *       return consultar("SELECT * FROM doadores WHERE tipo_sanguineo = ?", t.getRotulo());
 *   }
 *   public void inserir(Doador d) throws DAOException {
 *       int id = inserirRetornandoId("INSERT INTO doadores (nome, ...) VALUES (?, ...)",
 *               d.getNome(), ...);
 *       d.setId(id);
 *   }
 * </pre>
 *
 * PARAMETROS dos helpers: passe valores JA CONVERTIDOS para o formato do banco:
 * LocalDate -> data.toString() (ISO yyyy-MM-dd) | TipoSanguineo -> getRotulo()
 * | Sexo -> getCodigo() | StatusCampanha/NivelEstoque -> name()
 * | null e aceito (vira NULL no banco).
 *
 * Todo acesso usa PreparedStatement + try-with-resources, e qualquer
 * SQLException e convertida em DAOException.
 *
 * Responsavel: Gabriel.
 * CONTRATO: assinaturas usadas por Pedro, Ricardo e Amon. Nao alterar sem avisar o grupo.
 *
 * @param <T> tipo do objeto de modelo (Doador, Receptor, Doacao)
 */
public abstract class BaseDAO<T> {

    // ------------------------------------------------------------------
    // Metodos ABSTRATOS: cada DAO filho implementa
    // ------------------------------------------------------------------

    /** @return nome da tabela no banco, ex: "doadores" */
    protected abstract String getTabela();

    /**
     * Converte a linha ATUAL do ResultSet em um objeto (nao chame rs.next() aqui).
     */
    protected abstract T mapear(ResultSet rs) throws SQLException;

    /** Insere o objeto no banco e preenche o id gerado no proprio objeto. */
    public abstract void inserir(T obj) throws DAOException;

    /** Atualiza todos os campos do objeto no banco (pelo id). */
    public abstract void atualizar(T obj) throws DAOException;

    // ------------------------------------------------------------------
    // Metodos PRONTOS: herdados por todos os DAOs
    // ------------------------------------------------------------------

    /** @return o objeto com o id informado, ou Optional.empty() se nao existir */
    public Optional<T> buscarPorId(int id) throws DAOException {
        // TODO(Gabriel): SELECT * FROM getTabela() WHERE id = ?  (usar consultar)
        throw new UnsupportedOperationException("TODO(Gabriel): BaseDAO.buscarPorId");
    }

    /** @return todos os registros da tabela, ordenados por id */
    public List<T> listarTodos() throws DAOException {
        // TODO(Gabriel)
        throw new UnsupportedOperationException("TODO(Gabriel): BaseDAO.listarTodos");
    }

    /** @return true se algum registro foi excluido */
    public boolean excluir(int id) throws DAOException {
        // TODO(Gabriel)
        throw new UnsupportedOperationException("TODO(Gabriel): BaseDAO.excluir");
    }

    // ------------------------------------------------------------------
    // Helpers PROTEGIDOS: para os DAOs filhos escreverem menos JDBC
    // ------------------------------------------------------------------

    /**
     * Executa um SELECT e converte cada linha com mapear().
     *
     * @return lista (possivelmente vazia, nunca null)
     */
    protected List<T> consultar(String sql, Object... parametros) throws DAOException {
        // TODO(Gabriel): Database.getConnection() + PreparedStatement + while(rs.next()) mapear(rs)
        throw new UnsupportedOperationException("TODO(Gabriel): BaseDAO.consultar");
    }

    /**
     * Executa INSERT/UPDATE/DELETE.
     *
     * @return quantidade de linhas afetadas
     */
    protected int executar(String sql, Object... parametros) throws DAOException {
        // TODO(Gabriel)
        throw new UnsupportedOperationException("TODO(Gabriel): BaseDAO.executar");
    }

    /**
     * Executa um INSERT e devolve o id gerado (AUTOINCREMENT).
     */
    protected int inserirRetornandoId(String sql, Object... parametros) throws DAOException {
        // TODO(Gabriel): usar Statement.RETURN_GENERATED_KEYS
        throw new UnsupportedOperationException("TODO(Gabriel): BaseDAO.inserirRetornandoId");
    }
}
