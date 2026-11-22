package com.doamais.teste;

import com.doamais.dao.BaseDAO;
import com.doamais.db.Database;
import com.doamais.exception.DAOException;
import com.doamais.exception.DoaMaisException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Teste da camada de persistencia (Database + BaseDAO + DAOException).
 */
public class PersistenciaTeste {

    public static void main(String[] args) throws DoaMaisException {
        // 1) Inicializa o banco SQLite e carrega o seed se estiver vazio
        Database.inicializar();
        System.out.println("== 1) Banco SQLite inicializado com sucesso");

        // 2) HERANCA + POLIMORFISMO: subclasse anonima de BaseDAO<String>
        BaseDAO<String> receptores = new BaseDAO<String>() {
            @Override
            protected String getTabela() {
                return "receptores";
            }

            // POLIMORFISMO: listarTodos() e buscarPorId() da BaseDAO chamam este mapear()
            @Override
            protected String mapear(ResultSet rs) throws SQLException {
                return rs.getInt("id") + " - " + rs.getString("nome_paciente")
                        + " (" + rs.getInt("doacoes_recebidas") + "/" + rs.getInt("meta_doacoes") + ")";
            }

            @Override
            public void inserir(String obj) throws DAOException {
                throw new DAOException("Operacao nao usada neste teste");
            }

            @Override
            public void atualizar(String obj) throws DAOException {
                throw new DAOException("Operacao nao usada neste teste");
            }
        };

        System.out.println("== 2) receptores.listarTodos():");
        List<String> todos = receptores.listarTodos();
        for (String r : todos) {
            System.out.println("   " + r);
        }
        Optional<String> primeiro = receptores.buscarPorId(1);
        System.out.println("   buscarPorId(1)    = " + primeiro.orElse("(nao encontrado)"));
        System.out.println("   buscarPorId(9999) = " + receptores.buscarPorId(9999).orElse("(nao encontrado)"));

        // 3) TRATAMENTO DE EXCECAO: CPF repetido (UNIQUE) vira DAOException amigavel
        try {
            new DoadorCpfTesteDAO().inserir("12345678901");
            System.out.println("== 3) ERRO: deveria ter lancado DAOException");
        } catch (DAOException e) {
            System.out.println("== 3) DAOException capturada: " + e.getMessage());
        }

        System.out.println("OK");
    }

    /** Subclasse interna de BaseDAO para testar violacao de UNIQUE no CPF. */
    private static class DoadorCpfTesteDAO extends BaseDAO<String> {

        @Override
        protected String getTabela() {
            return "doadores";
        }

        @Override
        protected String mapear(ResultSet rs) throws SQLException {
            return rs.getString("nome");
        }

        @Override
        public void inserir(String cpf) throws DAOException {
            inserirRetornandoId("INSERT INTO doadores (nome, cpf, data_nascimento, sexo, peso, "
                    + "tipo_sanguineo) VALUES (?, ?, ?, ?, ?, ?)",
                    "Teste Duplicado", cpf, "1990-01-01", "M", 70.0, "O+");
        }

        @Override
        public void atualizar(String obj) throws DAOException {
            throw new DAOException("Operacao nao usada neste teste");
        }
    }
}
