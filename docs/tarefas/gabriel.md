# Tarefa: Gabriel Ridolfi Pupim · Infraestrutura de dados, persistência e exceções

- **Branch:** `feat/gabriel-dados`
- **Marcador no código:** `TODO(Gabriel)`
- **Prioridade:** 🔴 **alta, fazer primeiro.** `BaseDAO` é usada pelos DAOs de Pedro, Ricardo e Amon.
- **Conceitos do edital que você demonstra:** leitura e gravação (banco de dados SQLite via JDBC), herança, polimorfismo, tratamento de exceção, coleções.

## Contexto

Você é responsável por tudo que envolve **guardar e recuperar dados**. A conexão com o SQLite, a criação das tabelas e a população automática dos dados iniciais da demonstração (`Database.java`), a **base comum dos DAOs** (`BaseDAO<T>`), as exceções do projeto (`DoaMaisException`, `DAOException`, `ValidacaoException`) e a classe de teste (`PersistenciaTeste.java`) compõem o seu módulo.

## O que fazer

### 1. `src/com/doamais/dao/BaseDAO.java`
Leia o Javadoc da classe (ele define as regras). Implemente:
- `consultar(String sql, Object... parametros)` → `List<T>`:
  - `Database.getConnection()` + `PreparedStatement` + `ResultSet`, tudo em **try-with-resources**;
  - `ps.setObject(i + 1, parametros[i])` para cada parâmetro (os valores já chegam convertidos);
  - `while (rs.next()) lista.add(mapear(rs));` ← **polimorfismo**: cada DAO filho tem seu `mapear`;
  - `catch (SQLException e)` → `throw new DAOException("Erro ao consultar " + getTabela(), e);`
- `executar(String sql, Object... parametros)` → linhas afetadas (`executeUpdate`).
- `inserirRetornandoId(String sql, Object... parametros)` → `prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)`, `executeUpdate()`, `getGeneratedKeys()`. Se não houver chave, lance `DAOException`.
  - **Atenção:** se a `SQLException` for de violação de UNIQUE (mensagem contém `"UNIQUE"`), lance `DAOException` com mensagem amigável (`"Registro duplicado em " + getTabela()`). O Pedro depende disso para CPF repetido.
- `buscarPorId(int id)` → `consultar("SELECT * FROM " + getTabela() + " WHERE id = ?", id)` e devolve `Optional` do primeiro (ou `Optional.empty()`).
- `listarTodos()` → `SELECT * FROM <tabela> ORDER BY id`.
- `excluir(int id)` → `executar("DELETE FROM ... WHERE id = ?", id) > 0`.

### 2. `src/com/doamais/db/Database.java`
- Gerenciamento da conexão JDBC com `database/doamais.db` (`PRAGMA foreign_keys = ON` e `busy_timeout`).
- Criação automática das 4 tabelas (`doadores`, `receptores`, `doacoes`, `estoque_hemoes`) no primeiro boot.
- Carga automática dos dados iniciais da demonstração (doadores, campanha da Maria Clara com 3/5 bolsas e doações) quando a tabela `doadores` estiver vazia.

### 3. Teste: `src/com/doamais/teste/PersistenciaTeste.java`
`main` que:
1. chama `Database.inicializar()` (cria tabelas e popula os dados iniciais se o banco estiver vazio);
2. cria uma subclasse **anônima** de `BaseDAO` para a tabela `receptores` (`BaseDAO<String>`), e imprime `listarTodos()` e `buscarPorId(1)`. Isso prova a herança e o polimorfismo **sem depender** dos DAOs dos colegas;
3. provoca uma violação de `UNIQUE` no CPF e imprime a `DAOException` capturada.

Rodar: `build.bat com.doamais.teste.PersistenciaTeste`.

## Critérios de pronto
- [x] Nenhum `TODO(Gabriel)` restante; `build.bat compilar` OK.
- [x] `PersistenciaTeste` roda sem erro e mostra os dados iniciais.
- [x] Banco apagado + servidor iniciado → tabelas e dados da demo carregados automaticamente.

## Na apresentação, saiba explicar
- Por que uma **classe abstrata genérica** (`BaseDAO<T>`) evita código repetido e como o `<T>` funciona.
- Como `listarTodos()` é **polimórfico**: escrito uma vez, executa o `mapear()` de cada DAO filho.
- A **hierarquia de exceções** e por que converter `SQLException` em `DAOException` (isolamento de camadas).
- Como o `PreparedStatement` evita **SQL Injection** e por que usar `try-with-resources` para fechar conexões.
