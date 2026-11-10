package com.doamais.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Infraestrutura de banco de dados do DoaMais.
 *
 * Gerencia o ciclo de vida da conexao JDBC com o SQLite (arquivo local
 * database/doamais.db) e cria automaticamente as tabelas no primeiro boot.
 *
 * Uso nos DAOs:
 * <pre>
 *   try (Connection c = Database.getConnection();
 *        PreparedStatement ps = c.prepareStatement("SELECT ...")) { ... }
 * </pre>
 * Cada chamada a getConnection() devolve uma conexao nova, que deve ser
 * fechada pelo chamador (try-with-resources). Isso evita problemas de
 * concorrencia, ja que o HttpServer atende requisicoes em varias threads.
 */
public final class Database {

    private static final String DB_DIR = "database";
    private static final String DB_FILE = "doamais.db";
    private static final String URL = "jdbc:sqlite:" + DB_DIR + "/" + DB_FILE;

    private Database() {
        // classe utilitaria: nao instanciavel
    }

    /**
     * Abre uma nova conexao com o SQLite, com chaves estrangeiras habilitadas
     * (o SQLite as desabilita por padrao a cada conexao).
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            st.execute("PRAGMA busy_timeout = 5000");
        }
        return conn;
    }

    /**
     * Cria a pasta do banco e todas as tabelas, se ainda nao existirem.
     * Deve ser chamado uma unica vez na inicializacao (Main.java).
     * E idempotente: pode ser chamado varias vezes sem perder dados.
     */
    public static void inicializar() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "Driver SQLite JDBC nao encontrado. Verifique a pasta lib/ no classpath.", e);
        }

        File dir = new File(DB_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("Nao foi possivel criar a pasta " + DB_DIR);
        }

        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {

            st.execute("""
                CREATE TABLE IF NOT EXISTS doadores (
                    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome               TEXT    NOT NULL,
                    cpf                TEXT    NOT NULL UNIQUE,
                    data_nascimento    TEXT    NOT NULL,            -- ISO yyyy-MM-dd
                    sexo               TEXT    NOT NULL CHECK (sexo IN ('M','F')),
                    peso               REAL    NOT NULL CHECK (peso > 0),
                    tipo_sanguineo     TEXT    NOT NULL CHECK (tipo_sanguineo IN
                                           ('A+','A-','B+','B-','AB+','AB-','O+','O-')),
                    telefone           TEXT,
                    email              TEXT,
                    data_ultima_doacao TEXT                         -- ISO yyyy-MM-dd, pode ser nula
                )""");

            st.execute("""
                CREATE TABLE IF NOT EXISTS receptores (
                    id                INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome_paciente     TEXT    NOT NULL,
                    hospital          TEXT    NOT NULL,
                    historia          TEXT,
                    tipo_sanguineo    TEXT    NOT NULL CHECK (tipo_sanguineo IN
                                          ('A+','A-','B+','B-','AB+','AB-','O+','O-')),
                    meta_doacoes      INTEGER NOT NULL CHECK (meta_doacoes > 0),
                    doacoes_recebidas INTEGER NOT NULL DEFAULT 0 CHECK (doacoes_recebidas >= 0),
                    status            TEXT    NOT NULL DEFAULT 'ATIVA'
                                          CHECK (status IN ('ATIVA','CONCLUIDA')),
                    data_criacao      TEXT    NOT NULL DEFAULT (date('now'))
                )""");

            st.execute("""
                CREATE TABLE IF NOT EXISTS doacoes (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    doador_id   INTEGER NOT NULL,
                    receptor_id INTEGER,                            -- nulo = estoque geral de reposicao
                    data_doacao TEXT    NOT NULL,                   -- ISO yyyy-MM-dd
                    local       TEXT,
                    FOREIGN KEY (doador_id)   REFERENCES doadores(id)   ON DELETE CASCADE,
                    FOREIGN KEY (receptor_id) REFERENCES receptores(id) ON DELETE SET NULL
                )""");

            // Quarta tabela: termometro do HEMOES (leitura com fallback em banco local)
            st.execute("""
                CREATE TABLE IF NOT EXISTS estoque_hemoes (
                    tipo_sanguineo TEXT PRIMARY KEY CHECK (tipo_sanguineo IN
                                       ('A+','A-','B+','B-','AB+','AB-','O+','O-')),
                    nivel          TEXT NOT NULL CHECK (nivel IN ('CRITICO','BAIXO','NORMAL','ALTO')),
                    atualizado_em  TEXT NOT NULL DEFAULT (datetime('now'))
                )""");

            st.execute("CREATE INDEX IF NOT EXISTS idx_doacoes_doador   ON doacoes(doador_id)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_doacoes_receptor ON doacoes(receptor_id)");

            // Valores iniciais do termometro (so insere se a linha ainda nao existir)
            st.execute("""
                INSERT OR IGNORE INTO estoque_hemoes (tipo_sanguineo, nivel) VALUES
                    ('A+','NORMAL'),('A-','BAIXO'),('B+','NORMAL'),('B-','CRITICO'),
                    ('AB+','ALTO'),('AB-','BAIXO'),('O+','NORMAL'),('O-','CRITICO')""");

        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao inicializar o banco de dados", e);
        }

        // TODO(Gabriel): carregar os dados da demonstracao quando o banco estiver vazio:
        //   try { CsvUtil.carregarDadosIniciais(); } catch (DoaMaisException e) { ... }
    }
}
