# Tarefa: Ricardo Irineu Sales Machado · Módulo de Campanhas e Receptores

- **Branch:** `feat/ricardo-campanhas`
- **Marcador no código:** `TODO(Ricardo)`
- **Conceitos do edital que você demonstra:** **herança**, **polimorfismo**, **encapsulamento**, tratamento de exceção, coleções.

## Contexto

Você cuida das **campanhas de reposição** de pacientes internados: hospital, relato da família, tipo sanguíneo, **meta de doações** e **acompanhamento automático do progresso** até o status **Concluída**. O `Receptor` também é uma `Pessoa` (como o `Doador` do Pedro), o que mostra **duas subclasses diferentes da mesma superclasse abstrata**. O enum `StatusCampanha` já existe no repositório.

## O que fazer

### 1. `src/com/doamais/model/Receptor.java`: completar os `TODO(Ricardo)`
- `getProgresso()`: `doacoesRecebidas * 100.0 / metaDoacoes`, limitado a 100.0 (`Math.min`). Se `metaDoacoes <= 0`, retorne 0.
- `getFaltam()`: `Math.max(0, metaDoacoes - doacoesRecebidas)`.
- `registrarDoacao()` (**encapsulamento**: única forma de mudar o progresso):
  - se `isConcluida()` → `throw new ValidacaoException("A campanha de " + nome + " já atingiu a meta.")`;
  - `doacoesRecebidas++`;
  - se `doacoesRecebidas >= metaDoacoes` → `status = StatusCampanha.CONCLUIDA`.
- `getDescricao()` (**@Override, polimorfismo**): `"<nome> – <hospital> – precisa de <tipo>"`.
- `validar()` (**@Override**): `validarDadosBasicos()` (herdado), hospital obrigatório, `metaDoacoes` entre 1 e 50, e `doacoesRecebidas` entre 0 e a meta.

### 2. `src/com/doamais/dao/ReceptorDAO.java`: completar os `TODO(Ricardo)`
Use os helpers herdados da `BaseDAO` (`consultar`, `executar`, `inserirRetornandoId`). Conversões em `docs/CONTRATOS.md` §3.
- `mapear(ResultSet rs)`: construtor **completo** de `Receptor` (`nome_paciente` → nome; `status` com `StatusCampanha.valueOf`; `data_criacao` com `LocalDate.parse`). `TipoSanguineo.deRotulo` lança `ValidacaoException`: capture e relance como `SQLException("Dado inválido no banco: ...", e)`.
- `inserir(Receptor r)`: todas as colunas exceto `id`, depois `r.setId(id)`.
- `atualizar(Receptor r)`: `UPDATE` de todos os campos pelo `id`; nenhuma linha afetada → `DAOException("Campanha não encontrada")`.
- `listarAtivas()` → `List<Receptor>` com `status = 'ATIVA'`, ordenada da **mais urgente para a menos urgente**: `ORDER BY (CAST(doacoes_recebidas AS REAL) / meta_doacoes) ASC, data_criacao ASC`. **A Maria usa este método no feed da home.**
- `atualizarProgresso(Receptor r)`: `UPDATE receptores SET doacoes_recebidas = ?, status = ? WHERE id = ?`. **O Amon usa este método.**

### 3. `src/com/doamais/handler/ReceptorHandler.java`: completar os `TODO(Ricardo)`
Rotas no Javadoc da classe e em `docs/CONTRATOS.md` §4 (campos: `nomePaciente`, `hospital`, `tipoSanguineo`, `metaDoacoes`, `historia`). Use `HtmlUtil.pagina`, `escapar`, `barraProgresso`, `opcoesTipoSanguineo`, `formatarData` e `FormUtil.get*()`.
- `get`:
  - `/campanhas/nova`: formulário de criação (`method="post"`), com `<textarea name="historia">`;
  - `/campanhas/ver?id=`: página da campanha com nome, hospital, tipo, história, data de criação, **"X de Y bolsas"**, `HtmlUtil.barraProgresso(r.getProgresso())`, **"Faltam N bolsas"**, status, e botão **"Quero doar"** → `/doacoes/nova?receptorId=ID`. Se concluída, troque o botão por um alerta "Meta atingida! Obrigado a todos os doadores." Id inexistente → `ValidacaoException("Campanha não encontrada")`;
  - outro caminho → 404.
- `post` em `/campanhas/nova`: cria com o **construtor de campanha nova**, chama `validar()`, `inserir`, e `redirecionar(ex, "/campanhas/ver?id=" + r.getId())`.
- Erros sobem como exceção (o `BaseHandler` mostra). Não use try/catch para exibir mensagens.

### 4. Teste: `src/com/doamais/teste/CampanhaTeste.java` (criar)
`main` que:
1. cria a campanha "Maria Clara", O+, meta 5, e chama `registrarDoacao()` 5 vezes, imprimindo progresso, faltam e status a cada vez (deve terminar **100% / CONCLUIDA**);
2. chama `registrarDoacao()` uma 6ª vez e imprime a mensagem da `ValidacaoException` capturada;
3. **Polimorfismo:** monta uma `List<Pessoa>` com um `Receptor` e um `Doador` (construtor completo) e chama `getDescricao()` e `validar()` em cada um. Comente que o método executado depende do tipo real do objeto. (O `Doador` é do Pedro; até ele subir, essa parte lança `TODO(Pedro)`. É esperado.)
4. (depende do Gabriel) `Database.inicializar()`, insere uma campanha, lista `listarAtivas()`, registra uma doação e grava com `atualizarProgresso`, e busca de novo por id conferindo o progresso.

Rodar: `build.bat com.doamais.teste.CampanhaTeste`.

## Não mexa em
`Pessoa` (Pedro), `BaseDAO` (Gabriel), `BaseHandler`/`HtmlUtil`/`FormUtil` (Maria). Não altere assinaturas de `Receptor`/`ReceptorDAO`: Amon e Maria dependem delas.

## Critérios de pronto
- [ ] Nenhum `TODO(Ricardo)` restante; `build.bat compilar` OK.
- [ ] `CampanhaTeste` partes 1 e 2 rodam sem depender de ninguém.
- [ ] Criar campanha pelo navegador e ver a página com o progresso correto; ao atingir a meta, status muda sozinho e ela sai do feed.

## Na apresentação, saiba explicar
- Como **duas classes diferentes herdam da mesma `Pessoa`** e o que cada uma acrescenta.
- O exemplo da `List<Pessoa>` chamando o `getDescricao()` certo de cada uma (**polimorfismo**).
- Por que `doacoesRecebidas` e `status` **não têm setter** (**encapsulamento**: a regra da meta fica protegida em `registrarDoacao()`).
- A ordenação por urgência no SQL de `listarAtivas()`.
