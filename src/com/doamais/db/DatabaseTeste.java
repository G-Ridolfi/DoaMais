package com.doamais.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/** Teste rapido: inicializa o banco e lista as tabelas criadas. */
public class DatabaseTeste {
    public static void main(String[] args) throws Exception {
        Database.inicializar();
        Database.inicializar(); // idempotente
        try (Connection c = Database.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' ORDER BY name")) {
            while (rs.next()) {
                System.out.println("Tabela: " + rs.getString(1));
            }
            try (ResultSet fk = st.executeQuery("PRAGMA foreign_keys")) {
                System.out.println("foreign_keys = " + fk.getInt(1));
            }
        }
    }
}
