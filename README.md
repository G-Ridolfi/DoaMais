# DoaMais

Plataforma comunitária de mobilização e apoio à doação de sangue (Projeto Integrador II).

**Stack:** Java puro (`com.sun.net.httpserver`), SQLite via JDBC, HTML/CSS/JS.

## Estrutura

```
src/com/doamais/
  Main.java              (Maria Vitória) inicialização e rotas
  db/Database.java       (Gabriel) conexão JDBC + criação das tabelas
  model/                 Doador, Receptor, Doacao
  dao/                   DoadorDAO, ReceptorDAO, DoacaoDAO
  handler/               Handlers HTTP
  util/                  FormUtil, HtmlUtil
lib/                     drivers JAR (SQLite JDBC)
database/doamais.db      gerado automaticamente (não vai pro Git)
```

## Como rodar

Requer JDK 17+.

```
build.bat          # compila e executa com.doamais.Main
build.bat teste    # testa a criação do banco
```

## Como usar o banco (para os DAOs)

No `main`, chame **uma vez**: `Database.inicializar();`

Nos DAOs:

```java
try (Connection c = Database.getConnection();
     PreparedStatement ps = c.prepareStatement("SELECT * FROM doadores WHERE id = ?")) {
    ps.setInt(1, id);
    ...
}
```

### Schema

| Tabela | Colunas principais |
|---|---|
| `doadores` | id, nome, cpf (único), data_nascimento, sexo (M/F), peso, tipo_sanguineo, telefone, email, data_ultima_doacao |
| `receptores` | id, nome_paciente, hospital, historia, tipo_sanguineo, meta_doacoes, doacoes_recebidas, status (ATIVA/CONCLUIDA), data_criacao |
| `doacoes` | id, doador_id → doadores, receptor_id → receptores (nulo = estoque geral), data_doacao, local |
| `estoque_hemoes` | tipo_sanguineo (PK), nivel (CRITICO/BAIXO/NORMAL/ALTO), atualizado_em |

Datas são texto ISO (`yyyy-MM-dd`) — use `LocalDate.toString()` / `LocalDate.parse()`.
Tipos sanguíneos válidos: `A+ A- B+ B- AB+ AB- O+ O-`.

## Divisão do grupo

| Integrante | Módulo |
|---|---|
| Gabriel Ridolfi Pupim | Banco de dados (`Database.java`) |
| Pedro Paulo Rodrigues da Silva | Doadores (Model/DAO/Handler) |
| Guilherme Caiado Richa | Motor de regras de aptidão |
| Ricardo Irineu Sales Machado | Receptores e campanhas |
| Amon Butk Rangel Alves | Doações e vínculo |
| Maria Vitória Poubel de Paula Pires | Frontend, `Main`, utilitários, README |

## Fluxo Git

- Nunca commitar direto na `main`.
- `git pull origin main` → `git checkout -b feat/seu-modulo` → commits → `git push -u origin feat/seu-modulo` → Pull Request.
