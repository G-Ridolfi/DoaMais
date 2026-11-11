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
 * Teste manual da camada de persistencia (Database + BaseDAO + DAOException).
 *
 * Nao depende dos DAOs dos colegas: usa uma subclasse ANONIMA de BaseDAO
 * para provar a HERANCA e o POLIMORFISMO (listarTodos() herdado chamando o
 * mapear() definido aqui).
 *
 * Rodar: build.bat com.doamais.teste.PersistenciaTeste
 * (apague database/doamais.db antes para testar a carga inicial do zero).
 *
 * Responsavel: Gabriel.
 */
public class PersistenciaTeste {

    public static void main(String[] args) throws DoaMaisException {
        // 1) Cria as tabelas e popula os dados iniciais da demonstracao se o banco estiver vazio
        Database.inicializar();
        System.out.println("== 1) Banco SQLite inicializado com sucesso");

        // 2) HERANCA: subclasse anonima de BaseDAO<String> para a tabela receptores
        BaseDAO<String> receptores = new BaseDAO<String>() {
            @Override
            protected String getTabela() {
                return "receptores";
            }

            // POLIMORFISMO: listarTodos()/buscarPorId() da BaseDAO chamam ESTE mapear()
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

        // 3) TRATAMENTO DE EXCECAO: CPF repetido vira DAOException amigavel
        try {
            new DoadorCpfTesteDAO().inserir("12345678901"); // CPF do Carlos Andrade
            System.out.println("== 3) ERRO: deveria ter lancado DAOException");
        } catch (DAOException e) {
            System.out.println("== 3) DAOException capturada: " + e.getMessage());
        }

        System.out.println("OK");
    }

    /**
     * Subclasse INTERNA de BaseDAO usada para provocar uma violacao de
     * UNIQUE no CPF e conferir a mensagem de inserirRetornandoId().
     */
    private static class DoadorCpfTesteDAO extends BaseDAO<String> {

        @Override
        protected String getTabela() {
            return "doadores";
        }

        @Override
        protected String mapear(ResultSet rs) throws SQLException {
            return rs.getString("nome");
        }

        /** Tenta inserir um doador ficticio com o CPF informado. */
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
