package com.doamais.teste;

import com.doamais.db.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Teste manual da infraestrutura de banco: inicializa duas vezes (deve ser
 * idempotente) e lista as tabelas criadas.
 *
 * Rodar: build.bat com.doamais.teste.DatabaseTeste
 *
 * Responsavel: Gabriel.
 */
public class DatabaseTeste {

    public static void main(String[] args) throws Exception {
        Database.inicializar();
        Database.inicializar(); // idempotente: nao pode dar erro nem duplicar dados

        try (Connection c = Database.getConnection();
             Statement st = c.createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' "
                            + "AND name NOT LIKE 'sqlite_%' ORDER BY name")) {
                while (rs.next()) {
                    System.out.println("Tabela: " + rs.getString(1));
                }
            }
            try (ResultSet fk = st.executeQuery("PRAGMA foreign_keys")) {
                System.out.println("foreign_keys = " + fk.getInt(1));
            }
            try (ResultSet est = st.executeQuery("SELECT COUNT(*) FROM estoque_hemoes")) {
                System.out.println("tipos no termometro = " + est.getInt(1));
            }
        }
        System.out.println("OK");
    }
}
