# Tarefa: Gabriel Ridolfi Pupim · Infraestrutura de dados, persistência e exceções

- **Branch:** `feat/gabriel-dados`
- **Marcador no código:** `TODO(Gabriel)`
- **Prioridade:** 🔴 **alta, fazer primeiro.** `BaseDAO` é usada pelos DAOs de Pedro, Ricardo e Amon.
- **Conceitos do edital que você demonstra:** leitura e gravação (banco + CSV), herança, polimorfismo, tratamento de exceção, coleções.

## Contexto

Você é responsável por tudo que envolve **guardar e recuperar dados**. A conexão com o SQLite e a criação das tabelas (`Database.java`) já estão prontas. Falta a **base comum dos DAOs** (`BaseDAO<T>`), a **leitura e gravação em CSV** (`CsvUtil`), a **carga dos dados da demonstração** e a **tela de exportação**. As exceções do projeto (`DoaMaisException`, `DAOException`, `ValidacaoException`) já existem e são suas para explicar na apresentação.

## O que fazer

### 1. `src/com/doamais/dao/BaseDAO.java`: completar os `TODO(Gabriel)`
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
- Crie um método privado auxiliar para preencher os parâmetros (evita repetir código).

> `getTabela()` vem do próprio código (nunca do usuário), por isso pode ser concatenado. Valores **sempre** via `?`.

### 2. `src/com/doamais/db/CsvUtil.java`: completar os `TODO(Gabriel)`
Formato descrito no Javadoc: separador `;`, UTF-8, cabeçalho com os nomes das colunas, campo vazio = NULL.
- Constante `Set<String> TABELAS_PERMITIDAS = Set.of("doadores", "receptores", "doacoes", "estoque_hemoes")` e um método `validarTabela(String)` que lança `ValidacaoException` para qualquer outra (proteção contra SQL Injection no nome da tabela).
- `exportar(String tabela, Writer destino)`:
  - `SELECT * FROM <tabela> ORDER BY 1`; cabeçalho a partir de `ResultSetMetaData`;
  - escapar campos com `;`, aspas ou quebra de linha (envolver em aspas duplas e duplicar aspas internas);
  - `null` → campo vazio. `SQLException` → `DAOException`; `IOException` → `DAOException`.
- `exportar(String tabela, File arquivo)`: abre um `BufferedWriter` UTF-8 (try-with-resources) e chama o método acima.
- `importar(String tabela, File arquivo)`:
  - lê com `BufferedReader` UTF-8; primeira linha = colunas;
  - valida cada nome de coluna (só letras, números e `_`), senão `ValidacaoException`;
  - monta `INSERT INTO <tabela> (c1, c2...) VALUES (?, ?...)` e usa `PreparedStatement` com **batch** dentro de **uma transação** (`setAutoCommit(false)` → `commit()`; em erro `rollback()` no `catch`);
  - linha com quantidade de campos diferente do cabeçalho → `ValidacaoException("Linha N do arquivo X: ...")`;
  - campo vazio → `setNull`. Retorna quantas linhas importou.
  - Escreva um parser de linha CSV que respeite aspas (método privado `List<String> dividirLinha(String)`).
- `carregarDadosIniciais()`: se `SELECT COUNT(*) FROM doadores` = 0, importa `dados/seed/doadores.csv`, `receptores.csv` e `doacoes.csv` **nessa ordem**. Se a pasta não existir, apenas não faz nada.

### 3. `src/com/doamais/db/Database.java`: completar o `TODO(Gabriel)` no fim de `inicializar()`
Chamar `CsvUtil.carregarDadosIniciais()` dentro de try/catch. Em caso de `DoaMaisException`, imprimir um aviso no console (`System.err`) **sem derrubar** o servidor.

### 4. Criar os dados da demonstração em `dados/seed/`
Arquivos CSV (separador `;`, com cabeçalho = nomes das colunas, **incluindo `id`** para as FKs baterem):
- `doadores.csv`: pelo menos 8 doadores variados (vários tipos sanguíneos, homens e mulheres). **Obrigatórios para a demo:**
  - **Carlos Andrade**, masculino, 35 anos, 80 kg, O+, `data_ultima_doacao = 2026-10-29` (doou há 25 dias da apresentação → **inapto**);
  - **Ana Souza**, feminino, 28 anos, 62 kg, O+, nunca doou (`data_ultima_doacao` vazia) → **apta**;
  - os 3 doadores que já doaram para a Maria Clara: a `data_ultima_doacao` de cada um deve ser igual à data da doação deles em `doacoes.csv`. Use datas de **julho/2026** (mais de 90 dias antes da apresentação), assim eles continuam aptos na demo;
  - CPFs fictícios de 11 dígitos, todos diferentes.
- `receptores.csv`: pelo menos 3 campanhas. **Obrigatória:** **Maria Clara**, 8 anos (citar na `historia`), **Hospital Estadual Infantil**, tipo **O+**, `meta_doacoes = 5`, `doacoes_recebidas = 3`, `status = ATIVA`. Inclua também uma campanha `CONCLUIDA`.
- `doacoes.csv`: as **3 doações** já recebidas pela Maria Clara (coerente com `doacoes_recebidas = 3`) + algumas para o estoque geral (`receptor_id` vazio).
- O termômetro (`estoque_hemoes`) já é populado pelo `Database` (O- e B- críticos); **não** crie CSV para ele.

### 5. `src/com/doamais/handler/ExportarHandler.java`: completar o `TODO(Gabriel)`
Ver Javadoc. Sem parâmetro → página (`HtmlUtil.pagina`) com um link por tabela. Com `?tabela=` → `CsvUtil.exportar` num `StringWriter` e `enviarResposta(ex, 200, "text/csv; charset=UTF-8", bytes)` com o header `Content-Disposition: attachment; filename="<tabela>.csv"` (adicione em `ex.getResponseHeaders()` **antes** de enviar).

### 6. Teste: `src/com/doamais/teste/PersistenciaTeste.java` (criar)
`main` que:
1. chama `Database.inicializar()` (carrega o seed se o banco estiver vazio);
2. cria uma subclasse **anônima** ou interna de `BaseDAO` para a tabela `receptores` que mapeia só `id` e `nome_paciente` (`BaseDAO<String>`), e imprime `listarTodos()` e `buscarPorId(1)`. Isso prova a herança e o polimorfismo **sem depender** dos DAOs dos colegas;
3. exporta `doadores` para `out/doadores.csv` e imprime quantas linhas o arquivo tem;
4. tenta exportar a tabela `"sqlite_master"` e imprime a mensagem da `ValidacaoException` capturada.

Rodar: `build.bat com.doamais.teste.PersistenciaTeste` (apague `database/doamais.db` antes para testar o seed do zero).

## Não mexa em
Arquivos de outros integrantes. O schema das tabelas pode ser alterado **só por você**, e se alterar, **atualize `docs/CONTRATOS.md`** e avise o grupo.

## Critérios de pronto
- [ ] Nenhum `TODO(Gabriel)` restante; `build.bat compilar` OK.
- [ ] `PersistenciaTeste` roda sem erro e mostra os dados do seed.
- [ ] Banco apagado + servidor iniciado → dados da demo carregados automaticamente.
- [ ] `/exportar?tabela=doadores` baixa um CSV que abre no Excel.

## Na apresentação, saiba explicar
- Por que uma **classe abstrata genérica** (`BaseDAO<T>`) evita código repetido e como o `<T>` funciona.
- Como `listarTodos()` é **polimórfico**: escrito uma vez, executa o `mapear()` de cada DAO filho.
- A **hierarquia de exceções** e por que converter `SQLException` em `DAOException` (isolamento de camadas).
- Como o `PreparedStatement` evita **SQL Injection** e por que a tabela do CSV é validada numa lista.
- **Transação** com commit/rollback na importação do CSV.
