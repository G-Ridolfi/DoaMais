# Tarefa: Amon Butk Rangel Alves · Módulo de Doações, Vínculo e Estoque

- **Branch:** `feat/amon-doacoes`
- **Marcador no código:** `TODO(Amon)`
- **Conceitos do edital que você demonstra:** **coleções** (principal: `List`, `Map`, `EnumMap`), **tratamento de exceção**, associação entre objetos, herança.

## Contexto

Você faz o **registro formal da doação**, vinculando o voluntário a uma **campanha específica** ou ao **estoque geral de reposição**. Também cuida do **termômetro do HEMOES** (`estoque_hemoes`) e das **estatísticas**. É o módulo que **integra o trabalho dos colegas**: usa o `Doador` (Pedro), o `MotorAptidao` (Guilherme), o `Receptor` (Ricardo) e a `BaseDAO` (Gabriel). É o "gran finale" da demo, quando a barra da Maria Clara vai de 3/5 para 4/5. O enum `NivelEstoque` já existe no repositório.

## O que fazer

### 1. `src/com/doamais/model/Doacao.java`: completar o `TODO(Amon)`
- `isDirecionada()` → `receptor != null`.
- Revise o Javadoc explicando a **associação** (uma `Doacao` *tem um* `Doador` e *pode ter* um `Receptor`).

### 2. `src/com/doamais/dao/DoacaoDAO.java`: completar os `TODO(Amon)`
Use os helpers herdados da `BaseDAO`. Conversões em `docs/CONTRATOS.md` §3.
- `mapear(ResultSet rs)`: monte a `Doacao` com o `Doador` e o `Receptor` completos. Sugestão: atributos `private final DoadorDAO doadorDAO = new DoadorDAO()` e `ReceptorDAO receptorDAO`; no `mapear`, `doadorDAO.buscarPorId(rs.getInt("doador_id"))`. Como `buscarPorId` lança `DAOException` e `mapear` só pode lançar `SQLException`, converta (`throw new SQLException(e.getMessage(), e)`). `receptor_id` nulo: use `rs.getObject("receptor_id") == null`.
- `inserir(Doacao d)`: `receptor_id` = `null` quando não for direcionada; depois `d.setId(id)`.
- `atualizar(Doacao d)`: `UPDATE` pelo id.
- `listarPorDoador(int)` e `listarPorReceptor(int)` → `List<Doacao>` (`ORDER BY data_doacao DESC, id DESC`).
- `listarRecentes(int limite)` → `... ORDER BY data_doacao DESC, id DESC LIMIT ?`.
- `contarPorTipo()` → `Map<TipoSanguineo, Integer>`:
  - crie um `EnumMap<TipoSanguineo, Integer>` e coloque **os 8 tipos com 0**;
  - faça `SELECT d.tipo_sanguineo, COUNT(*) FROM doacoes x JOIN doadores d ON d.id = x.doador_id GROUP BY d.tipo_sanguineo`. Esse SELECT não devolve `Doacao`, então use `Database.getConnection()` direto, com try-with-resources e `SQLException` → `DAOException`;
  - preencha o mapa. Comente **por que `EnumMap`** (ordem dos tipos, eficiência, chave tipada).

### 3. `src/com/doamais/dao/EstoqueDAO.java`: completar os `TODO(Amon)`
Não herda de `BaseDAO` (a chave é o tipo, não um id): use `Database.getConnection()` + `PreparedStatement` + try-with-resources.
- `carregarTermometro()` → `EnumMap<TipoSanguineo, NivelEstoque>` com os 8 tipos (`SELECT tipo_sanguineo, nivel FROM estoque_hemoes`; `NivelEstoque.valueOf`). Tipo ausente no banco → `NivelEstoque.NORMAL`. **A Maria usa este método na home.**
- `atualizarNivel(tipo, nivel)`: `UPDATE estoque_hemoes SET nivel = ?, atualizado_em = datetime('now') WHERE tipo_sanguineo = ?`.

### 4. `src/com/doamais/service/DoacaoService.java`: completar os `TODO(Amon)`
Siga **exatamente** os 7 passos do Javadoc da classe:
1. buscar o doador (`DoadorDAO.buscarPorId`); inexistente → `ValidacaoException("Doador não encontrado")`;
2. se `receptorId != null`, buscar o receptor; inexistente → `ValidacaoException("Campanha não encontrada")`;
3. `motor.verificarOuLancar(doador)`: pode lançar **`DoadorInaptoException`**;
4. se direcionada: `receptor.registrarDoacao()` (lança `ValidacaoException` se a campanha já estiver concluída);
5. `doacaoDAO.inserir(new Doacao(0, doador, receptor, data, local))`;
6. `doadorDAO.atualizarUltimaDoacao(doadorId, data)`;
7. se direcionada: `receptorDAO.atualizarProgresso(receptor)`.
- Data no futuro → `ValidacaoException`.
- **Não capture** as exceções aqui: elas sobem até o `BaseHandler`. Comente isso no código: é o ponto de explicação sobre **propagação de exceções**.

### 5. `src/com/doamais/handler/DoacaoHandler.java`: completar os `TODO(Amon)`
Rotas no Javadoc e em `docs/CONTRATOS.md` §4 (campos: `doadorId`, `receptorId`, `dataDoacao`, `local`). Use `HtmlUtil.pagina`, `escapar`, `formatarData` e `FormUtil.get*()`.
- `get`:
  - `/doacoes`: tabela das últimas 20 doações (data, doador, destino = nome do paciente ou "Estoque geral", local) + tabela de **doações por tipo sanguíneo** (`contarPorTipo`, percorrendo o `Map` com `entrySet()`). Botão "Registrar doação";
  - `/doacoes/nova[?receptorId=]`: formulário com `<select name="doadorId">` (todos os doadores, `DoadorDAO.listarTodos()`), `<select name="receptorId">` (opção `value=""` "Estoque geral" + campanhas `ReceptorDAO.listarAtivas()`, pré-selecionando o `receptorId` da URL), `dataDoacao` (`type="date"`, valor padrão hoje) e `local` (padrão "HEMOES – Vitória");
  - outro caminho → 404.
- `post` em `/doacoes/nova`: `service.registrar(FormUtil.getInt(form,"doadorId"), FormUtil.getIntOpcional(form,"receptorId"), FormUtil.getData(form,"dataDoacao",true), FormUtil.getTexto(form,"local",false))`. Sucesso → `redirecionar` para `/campanhas/ver?id=` (direcionada) ou `/doacoes`.

### 6. Teste: `src/com/doamais/teste/DoacaoTeste.java` (criar)
`main` que:
1. **Coleções sem banco:** cria algumas `Doacao` em memória (com `Doador` do construtor completo), guarda numa `List<Doacao>`, conta por tipo num `Map<TipoSanguineo, Integer>` (`merge`) e imprime; mostra `isDirecionada()`;
2. (depende de Gabriel + seed) `Database.inicializar()`; imprime o termômetro (`carregarTermometro`) e `contarPorTipo()`;
3. (depende de Gabriel, Pedro, Guilherme, Ricardo) cenário da demo com `DoacaoService`:
   - doador **inapto** (Carlos, doou há 25 dias) para a Maria Clara → capturar `DoadorInaptoException` e imprimir motivos + data;
   - doador **apto** (Ana) para a Maria Clara → imprimir o progresso antes e depois (**3/5 → 4/5, 60% → 80%**).

> Até os colegas subirem suas partes, os passos 2 e 3 lançam `UnsupportedOperationException: TODO(<Colega>)`. **É esperado. Não implemente o código deles.**

Rodar: `build.bat com.doamais.teste.DoacaoTeste`.

## Não mexa em
`Doador`/`DoadorDAO` (Pedro), `MotorAptidao` (Guilherme), `Receptor`/`ReceptorDAO` (Ricardo), `BaseDAO`/`Database` (Gabriel), `BaseHandler`/`HtmlUtil`/`FormUtil`/`Main` (Maria). Se precisar de um método que não existe neles, **peça ao colega**.

## Critérios de pronto
- [ ] Nenhum `TODO(Amon)` restante; `build.bat compilar` OK.
- [ ] `DoacaoTeste` parte 1 roda sozinho; partes 2 e 3 rodam quando os colegas subirem.
- [ ] Passo 4 da demo pelo navegador: doação para a Maria Clara faz a barra ir para **4/5 (80%)**; doador inapto é bloqueado com a mensagem certa.

## Na apresentação, saiba explicar
- Quando usar **`List`**, **`Map`** e **`EnumMap`**, e por que o termômetro usa `EnumMap`.
- Como o `DoacaoService` **integra os módulos** dos colegas (desenhe os 7 passos).
- Como a `DoadorInaptoException` do Guilherme **"sobe"** do service até a tela (propagação de exceções, `throws`).
- **Associação**: `Doacao` tem um `Doador` e um `Receptor` (que pode ser nulo).
