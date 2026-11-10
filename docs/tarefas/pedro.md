# Tarefa: Pedro Paulo Rodrigues da Silva · Módulo de Doadores

- **Branch:** `feat/pedro-doadores`
- **Marcador no código:** `TODO(Pedro)`
- **Conceitos do edital que você demonstra:** **herança** (principal), **polimorfismo** (sobrescrita), encapsulamento, coleções, tratamento de exceção.

## Contexto

Você cuida do **cadastro dos voluntários doadores**: dados pessoais, tipo sanguíneo e data da última doação. A superclasse abstrata `Pessoa` e os enums `TipoSanguineo` e `Sexo` já estão prontos no repositório como contrato do grupo, mas **são seus para explicar na apresentação**. Eles são o centro da herança do modelo (o Ricardo também herda de `Pessoa`).

## O que fazer

### 1. `src/com/doamais/model/Doador.java`: completar os `TODO(Pedro)`
- `getIdade(LocalDate referencia)`: `Period.between(dataNascimento, referencia).getYears()`. Se `dataNascimento` for `null`, retorne 0.
- `getDescricao()` (**@Override, polimorfismo**): `"<nome>, <idade> anos – <tipo>"`, ex.: `"Ana Souza, 28 anos – O+"`.
- `validar()` (**@Override**), lançando `ValidacaoException` com mensagem clara em português:
  1. `validarDadosBasicos()` (herdado de `Pessoa`: nome e tipo obrigatórios);
  2. CPF: remova tudo que não for dígito (`cpf.replaceAll("\\D", "")`), guarde o resultado no atributo, e exija **exatamente 11 dígitos**;
  3. data de nascimento obrigatória e **antes de hoje**;
  4. sexo obrigatório;
  5. peso > 0 e < 300;
  6. data da última doação, se informada, **não pode ser futura** nem anterior ao nascimento.
- **Não** coloque aqui as regras de aptidão (idade 18–69, peso ≥ 50): isso é do Guilherme. Aqui é só "dado bem preenchido".

### 2. `src/com/doamais/dao/DoadorDAO.java`: completar os `TODO(Pedro)`
Use os helpers herdados da `BaseDAO` (`consultar`, `executar`, `inserirRetornandoId`): você **não** precisa abrir conexão manualmente. Conversões em `docs/CONTRATOS.md` §3.
- `mapear(ResultSet rs)`: ler todas as colunas e chamar o **construtor completo** de `Doador`. Datas: `rs.getString(...)` → `LocalDate.parse` (cuidado com `null` em `data_ultima_doacao`). `TipoSanguineo.deRotulo` e `Sexo.deCodigo` lançam `ValidacaoException`: capture e relance como `new SQLException("Dado inválido no banco: " + e.getMessage(), e)`.
- `inserir(Doador d)`: `INSERT` com todas as colunas exceto `id`, via `inserirRetornandoId`, depois `d.setId(id)`. CPF duplicado: capture a `DAOException` da `BaseDAO` e relance com a mensagem `"Já existe um doador cadastrado com o CPF " + cpf`. Uma alternativa melhor é checar antes com `buscarPorCpf`.
- `atualizar(Doador d)`: `UPDATE ... WHERE id = ?`. Se nenhuma linha for afetada → `DAOException("Doador não encontrado")`.
- `buscarPorCpf(String cpf)` → `Optional<Doador>`.
- `listarPorTipo(TipoSanguineo tipo)` → `List<Doador>` ordenada por nome.
- `atualizarUltimaDoacao(int doadorId, LocalDate data)`: `UPDATE doadores SET data_ultima_doacao = ? WHERE id = ?`. **O Amon depende deste método.**

### 3. `src/com/doamais/handler/DoadorHandler.java`: completar os `TODO(Pedro)`
Rotas e nomes de campos estão no Javadoc da classe e em `docs/CONTRATOS.md` §4. Use **sempre** `HtmlUtil.pagina(...)` para montar a página, `HtmlUtil.escapar(...)` em todo texto, `HtmlUtil.opcoesTipoSanguineo(...)` no select de tipo e `FormUtil.get*()` para ler campos.
- `get`:
  - `/doadores`: tabela com nome, idade, tipo, sexo, última doação (`HtmlUtil.formatarData`), links "Editar" e "Pré-triagem" (`/aptidao?doadorId=ID`). Um `<select>` de filtro `?tipo=` (vazio = todos) usa `listarPorTipo` ou `listarTodos`. Botão "Novo doador";
  - `/doadores/novo`: formulário vazio (`method="post"`);
  - `/doadores/editar?id=`: formulário preenchido + campo oculto `id`. Id inexistente → `ValidacaoException("Doador não encontrado")`;
  - outro caminho → `enviarHtml(ex, 404, ...)`.
- `post`:
  - `/doadores/novo` e `/doadores/editar`: montar o `Doador` a partir do `form`, chamar **`validar()`**, depois `inserir` ou `atualizar`, e `redirecionar(ex, "/doadores")`.
  - Use um método privado `Doador lerFormulario(Map<String,String> form)` para não repetir código.
- Erros: **não** use try/catch para mostrar mensagem; deixe a `ValidacaoException`/`DAOException` subir. O `BaseHandler` mostra o alerta.

### 4. Teste: `src/com/doamais/teste/DoadorTeste.java` (criar)
`main` que:
1. cria uma `List<Pessoa>` com um `Doador` e chama `getDescricao()` em cada item (mostra o **polimorfismo**);
2. testa `validar()` com: um doador válido (ok), CPF com 10 dígitos, peso negativo e nome vazio. Para cada caso inválido, capture a `ValidacaoException` e imprima a mensagem;
3. testa `getIdade(LocalDate.of(2026, 11, 23))` para alguém nascido em 1990-05-10 (esperado: 36);
4. (depende do Gabriel) `Database.inicializar()`, insere um doador com `DoadorDAO`, busca por CPF, lista por tipo e tenta inserir o mesmo CPF de novo, imprimindo a mensagem de erro.

Rodar: `build.bat com.doamais.teste.DoadorTeste`.

## Não mexa em
`Pessoa`, `TipoSanguineo` e `Sexo` são contratos usados por todos. Pode melhorar Javadoc, mas **não** altere assinaturas. Não edite arquivos de outros integrantes.

## Critérios de pronto
- [ ] Nenhum `TODO(Pedro)` restante; `build.bat compilar` OK.
- [ ] `DoadorTeste` roda (as partes 1 a 3 independem dos colegas).
- [ ] Com Gabriel e Maria na `main`: cadastrar, listar, filtrar e editar doadores pelo navegador; dado inválido mostra alerta sem derrubar o servidor.

## Na apresentação, saiba explicar
- Diferença entre **classe abstrata** e concreta; por que `new Pessoa()` não compila.
- O que é **herança** (`Doador extends Pessoa`, `DoadorDAO extends BaseDAO<Doador>`, `DoadorHandler extends BaseHandler`).
- O que é **sobrescrita** (`@Override` em `getDescricao`/`validar`) e o exemplo da `List<Pessoa>` (**polimorfismo**).
- Por que usar **enum** em vez de `String` para tipo sanguíneo e sexo.
- **Encapsulamento**: atributos privados + getters/setters.
