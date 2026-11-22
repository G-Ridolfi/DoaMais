package com.doamais.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * LEITURA E GRAVACAO (JDBC / SQLite): gerencia a conexao com database/doamais.db,
 * cria as tabelas no boot e popula os dados iniciais da demonstracao.
 */
public final class Database {

    private static final String DB_DIR = "database";
    private static final String DB_FILE = "doamais.db";
    private static final String URL = "jdbc:sqlite:" + DB_DIR + "/" + DB_FILE;

    private Database() {
    }

    /** Abre nova conexao SQLite ativando chaves estrangeiras (desligadas por padrao no SQLite). */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            st.execute("PRAGMA busy_timeout = 5000");
        }
        return conn;
    }

    /** Idempotente: cria a pasta, as 4 tabelas e carrega o seed se o banco estiver vazio. */
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
                    data_nascimento    TEXT    NOT NULL,
                    sexo               TEXT    NOT NULL CHECK (sexo IN ('M','F')),
                    peso               REAL    NOT NULL CHECK (peso > 0),
                    tipo_sanguineo     TEXT    NOT NULL CHECK (tipo_sanguineo IN
                                           ('A+','A-','B+','B-','AB+','AB-','O+','O-')),
                    telefone           TEXT,
                    email              TEXT,
                    data_ultima_doacao TEXT
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
                    receptor_id INTEGER,
                    data_doacao TEXT    NOT NULL,
                    local       TEXT,
                    FOREIGN KEY (doador_id)   REFERENCES doadores(id)   ON DELETE CASCADE,
                    FOREIGN KEY (receptor_id) REFERENCES receptores(id) ON DELETE SET NULL
                )""");

            st.execute("""
                CREATE TABLE IF NOT EXISTS estoque_hemoes (
                    tipo_sanguineo TEXT PRIMARY KEY CHECK (tipo_sanguineo IN
                                       ('A+','A-','B+','B-','AB+','AB-','O+','O-')),
                    nivel          TEXT NOT NULL CHECK (nivel IN ('CRITICO','BAIXO','NORMAL','ALTO')),
                    atualizado_em  TEXT NOT NULL DEFAULT (datetime('now'))
                )""");

            st.execute("CREATE INDEX IF NOT EXISTS idx_doacoes_doador   ON doacoes(doador_id)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_doacoes_receptor ON doacoes(receptor_id)");

            // Termometro inicial do HEMOES (O- e B- em nivel CRITICO)
            st.execute("""
                INSERT OR IGNORE INTO estoque_hemoes (tipo_sanguineo, nivel) VALUES
                    ('A+','NORMAL'),('A-','BAIXO'),('B+','NORMAL'),('B-','CRITICO'),
                    ('AB+','ALTO'),('AB-','BAIXO'),('O+','NORMAL'),('O-','CRITICO')""");

            carregarDadosIniciais(st);

        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao inicializar o banco de dados", e);
        }
    }

    /** Seed da demonstracao: so insere se a tabela doadores estiver vazia (COUNT = 0). */
    private static void carregarDadosIniciais(Statement st) throws SQLException {
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM doadores")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        st.execute("""
            INSERT INTO doadores (id, nome, cpf, data_nascimento, sexo, peso, tipo_sanguineo, telefone, email, data_ultima_doacao) VALUES
                (1,  'Carlos Andrade',   '12345678901', '1991-03-15', 'M', 80.0, 'O+',  '(27) 99811-2233', 'carlos.andrade@email.com',   '2026-10-29'),
                (2,  'Ana Souza',        '23456789012', '1998-06-10', 'F', 62.0, 'O+',  '(27) 99722-3344', 'ana.souza@email.com',        NULL),
                (3,  'Bruno Lima',       '34567890123', '1985-09-22', 'M', 78.5, 'O+',  '(27) 99633-4455', 'bruno.lima@email.com',       '2026-07-05'),
                (4,  'Juliana Ferreira', '45678901234', '1993-01-30', 'F', 58.0, 'O-',  '(27) 99544-5566', 'juliana.ferreira@email.com', '2026-07-12'),
                (5,  'Rafael Costa',     '56789012345', '1979-11-03', 'M', 92.0, 'O+',  '(27) 99455-6677', 'rafael.costa@email.com',     '2026-07-19'),
                (6,  'Fernanda Rocha',   '67890123456', '2000-04-18', 'F', 55.0, 'A+',  '(27) 99366-7788', 'fernanda.rocha@email.com',   '2026-06-20'),
                (7,  'Lucas Martins',    '78901234567', '1996-08-25', 'M', 70.0, 'B-',  '(27) 99277-8899', 'lucas.martins@email.com',    '2026-05-14'),
                (8,  'Patrícia Gomes',   '89012345678', '1988-12-07', 'F', 66.0, 'AB+', '(27) 99188-9900', 'patricia.gomes@email.com',   NULL),
                (9,  'Thiago Alves',     '90123456789', '2002-02-14', 'M', 84.0, 'A-',  '(27) 99099-0011', 'thiago.alves@email.com',     '2026-08-02'),
                (10, 'Camila Ribeiro',   '01234567890', '1990-10-01', 'F', 60.0, 'B+',  '(27) 98900-1122', 'camila.ribeiro@email.com',   '2026-08-15'),
                (11, 'Marcos Pereira',   '11223344556', '1983-05-27', 'M', 88.0, 'B-',  '(27) 98811-2233', 'marcos.pereira@email.com',   '2026-05-20')
            """);

        st.execute("""
            INSERT INTO receptores (id, nome_paciente, hospital, historia, tipo_sanguineo, meta_doacoes, doacoes_recebidas, status, data_criacao) VALUES
                (1, 'Maria Clara',    'Hospital Estadual Infantil',                   'Maria Clara tem 8 anos e está em tratamento contra a leucemia. Ela precisa de transfusões frequentes e cada bolsa doada ajuda a manter o tratamento em dia.', 'O+', 5, 3, 'ATIVA',     '2026-06-28'),
                (2, 'José Henrique',  'Hospital Universitário Cassiano Antônio Moraes','José Henrique, 52 anos, vai passar por uma cirurgia cardíaca e precisa repor o estoque de sangue A- usado no procedimento.',                                  'A-', 4, 1, 'ATIVA',     '2026-07-25'),
                (3, 'Lourdes Batista','Santa Casa de Misericórdia de Vitória',        'Dona Lourdes, 67 anos, sofreu um acidente e precisou de transfusões. Graças aos doadores a campanha foi concluída.',                                         'B-', 2, 2, 'CONCLUIDA', '2026-05-02')
            """);

        st.execute("""
            INSERT INTO doacoes (id, doador_id, receptor_id, data_doacao, local) VALUES
                (1, 3,  1,    '2026-07-05', 'HEMOES - Vitória'),
                (2, 4,  1,    '2026-07-12', 'HEMOES - Vitória'),
                (3, 5,  1,    '2026-07-19', 'HEMOES - Vitória'),
                (4, 7,  3,    '2026-05-14', 'HEMOES - Vitória'),
                (5, 11, 3,    '2026-05-20', 'Hemocentro Regional de Colatina'),
                (6, 9,  2,    '2026-08-02', 'HEMOES - Vitória'),
                (7, 6,  NULL, '2026-06-20', 'Hemocentro Regional de Linhares'),
                (8, 10, NULL, '2026-08-15', 'HEMOES - Vitória'),
                (9, 1,  NULL, '2026-10-29', 'HEMOES - Vitória')
            """);
    }
}
