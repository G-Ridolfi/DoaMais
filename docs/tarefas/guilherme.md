# Tarefa: Guilherme Caiado Richa · Motor de Regras de Aptidão

- **Branch:** `feat/guilherme-aptidao`
- **Marcador no código:** `TODO(Guilherme)`
- **Conceitos do edital que você demonstra:** **polimorfismo** (principal, via interface), coleções, tratamento de exceção.

## Contexto

Você implementa a **pré-triagem do doador** segundo os critérios do Ministério da Saúde:
- **idade** de 18 a 69 anos;
- **peso** mínimo de 50 kg;
- **intervalo** mínimo desde a última doação: **60 dias para homens** e **90 dias para mulheres**.

O sistema informa a **data exata** em que o voluntário estará liberado. Cada critério é **uma classe separada que implementa a interface `RegraAptidao`**. O `MotorAptidao` percorre uma lista dessas regras **sem saber qual é qual**. Esse é o exemplo mais claro de polimorfismo do projeto.

Já existem no repositório (contrato, seus para explicar): a interface `RegraAptidao`, a classe `ResultadoAptidao` (com as fábricas `apto()` e `inapto(motivo, data)`) e a `DoadorInaptoException`.

## O que fazer

### 1. Criar `src/com/doamais/service/RegraIdade.java`
`public class RegraIdade implements RegraAptidao`, com constantes `IDADE_MINIMA = 18` e `IDADE_MAXIMA = 69`.
- `avaliar(doador, hoje)` usa `doador.getIdade(hoje)` (método do Pedro).
- Menor de 18 → `inapto("Idade mínima para doar é 18 anos", dataNascimento.plusYears(18))`.
- Maior de 69 → `inapto("Idade máxima para doar é 69 anos", null)` (sem data de liberação).
- Senão → `apto()`.

### 2. Criar `src/com/doamais/service/RegraPeso.java`
`implements RegraAptidao`, constante `PESO_MINIMO = 50.0`. Abaixo de 50 kg → `inapto("Peso mínimo para doar é 50 kg", null)`.

### 3. Criar `src/com/doamais/service/RegraIntervalo.java`
`implements RegraAptidao`, constantes `DIAS_HOMEM = 60` e `DIAS_MULHER = 90`.
- Nunca doou (`getDataUltimaDoacao() == null`) → `apto()`.
- Intervalo pelo `Sexo` (use `switch` no enum).
- `liberacao = ultimaDoacao.plusDays(intervalo)`. Se `hoje.isBefore(liberacao)` → `inapto("Intervalo mínimo de " + intervalo + " dias desde a última doação (" + dias + " dias passados)", liberacao)`. Calcule os dias com `ChronoUnit.DAYS.between`.
- Senão → `apto()`.

> Escreva Javadoc em cada regra citando o critério oficial. Em cada `avaliar` use `@Override`.

### 4. `src/com/doamais/service/MotorAptidao.java`: completar os `TODO(Guilherme)`
- Construtor padrão: `this(List.of(new RegraIdade(), new RegraPeso(), new RegraIntervalo()));` (apague a linha provisória).
- `avaliar(Doador doador, LocalDate hoje)`:
  - `List<String> motivos = new ArrayList<>()`;
  - `for (RegraAptidao regra : regras)`: chama `regra.avaliar(doador, hoje)` ← **comente aqui que isso é polimorfismo**;
  - se inapto: `motivos.addAll(r.getMotivos())`, e guarda a **data de liberação mais distante** (ignorando `null`);
  - retorna `ResultadoAptidao.apto()` se `motivos` estiver vazio; senão `new ResultadoAptidao(false, motivos, dataMaisDistante)`.
- `verificarOuLancar(Doador doador)`: avalia hoje e, se inapto, `throw new DoadorInaptoException(r.getMotivos(), r.getDataLiberacao())`. **O Amon usa este método** antes de registrar uma doação.

### 5. `src/com/doamais/handler/AptidaoHandler.java`: completar os `TODO(Guilherme)`
Ver Javadoc da classe e `docs/CONTRATOS.md` §4 (campos: `dataNascimento`, `sexo`, `peso`, `dataUltimaDoacao`). Use `HtmlUtil.pagina`, `HtmlUtil.alerta`, `HtmlUtil.formatarData` e `FormUtil.get*()`.
- `get`:
  - `/aptidao` sem parâmetro: formulário de pré-triagem (funciona **sem cadastro**) com um texto curto explicando os critérios;
  - `/aptidao?doadorId=3`: busca o doador com `new DoadorDAO().buscarPorId(id)` (inexistente → `ValidacaoException`) e mostra o resultado direto.
- `post`: monta um `Doador` **temporário** (construtor vazio + setters; nome `"Visitante"`, tipo qualquer), chama `motor.avaliar(doador)` e mostra o resultado.
- Método privado `String renderizarResultado(ResultadoAptidao r)`:
  - apto → `alerta("sucesso", "Você está apto para doar! ...")` + link para a home;
  - inapto → `alerta("erro", ...)` com uma `<ul>` dos motivos e, se houver, `"Liberado para doar a partir de dd/mm/aaaa"`.
- O `MotorAptidao` pode ser um atributo `private final` do handler.

### 6. Teste: `src/com/doamais/teste/AptidaoTeste.java` (criar)
Use **data fixa** `LocalDate hoje = LocalDate.of(2026, 11, 23)` (dia da apresentação). Crie doadores com o construtor completo de `Doador` e imprima o resultado do motor para cada cenário:

| Cenário | Esperado |
|---|---|
| Homem, 35 anos, 80 kg, doou em 2026-10-29 (há 25 dias) | **inapto**, liberado em **28/12/2026** |
| Mulher, 28 anos, 62 kg, nunca doou | **apto** |
| 17 anos, 70 kg | inapto, liberado no 18º aniversário |
| 70 anos, 75 kg | inapto, sem data |
| 30 anos, 45 kg | inapto (peso), sem data |
| Mulher, doou há 60 dias | inapto (precisa 90) |
| 17 anos **e** 45 kg | inapto com **2 motivos** |

Imprima também `verificarOuLancar` do primeiro cenário dentro de try/catch, mostrando a mensagem da `DoadorInaptoException`.

> `Doador.getIdade` é do **Pedro**. Até ele subir, o teste lança `UnsupportedOperationException: TODO(Pedro)`. **Isso é esperado; não implemente a parte dele.** A `RegraPeso` e a `RegraIntervalo` podem ser testadas isoladamente antes disso.

Rodar: `build.bat com.doamais.teste.AptidaoTeste`.

## Não mexa em
`RegraAptidao`, `ResultadoAptidao` e `DoadorInaptoException` são contratos. Pode melhorar Javadoc, mas não mude assinaturas. Não edite `Doador` (Pedro).

## Critérios de pronto
- [ ] Nenhum `TODO(Guilherme)` restante; `build.bat compilar` OK.
- [ ] Todos os cenários da tabela dão o resultado esperado (após o Pedro subir `getIdade`).
- [ ] `/aptidao` funciona no navegador sem cadastro e com `?doadorId=`.

## Na apresentação, saiba explicar
- O que é uma **interface** e a diferença para classe abstrata.
- Como a mesma chamada `regra.avaliar(...)` executa códigos diferentes (**polimorfismo**) e por que adicionar uma regra nova não exige mudar o motor (aberto/fechado).
- Por que `List<RegraAptidao>` (**coleção**) e como a data de liberação final é a mais distante.
- O cálculo de datas com `LocalDate.plusDays` e `ChronoUnit.DAYS.between`.
- A `DoadorInaptoException` como **exceção própria com dados extras** (motivos + data).
