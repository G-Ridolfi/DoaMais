# Regras do grupo DoaMais (lidas automaticamente pelo Antigravity)

Projeto: **DoaMais**, plataforma comunitária de mobilização para doação de sangue.
Disciplinas: **Projeto Integrador II** e **Linguagem de Programação Orientada a Objetos (LPOO)**.
Repositório: https://github.com/G-Ridolfi/DoaMais

---

## 1. Comando "faça a tarefa de &lt;nome&gt;"

Quando o usuário pedir algo como *"faça a tarefa do Pedro"*, *"faça a tarefa destinada à Maria Vitória"* ou *"faça minha tarefa, sou o Amon"*, siga **exatamente** este roteiro.

### Mapa de integrantes

| Nome (como o usuário pode chamar) | Arquivo da tarefa | Branch | Marcador no código |
|---|---|---|---|
| Gabriel, Gabriel Ridolfi | `docs/tarefas/gabriel.md` | `feat/gabriel-dados` | `TODO(Gabriel)` |
| Pedro, Pedro Paulo | `docs/tarefas/pedro.md` | `feat/pedro-doadores` | `TODO(Pedro)` |
| Guilherme, Guilherme Caiado | `docs/tarefas/guilherme.md` | `feat/guilherme-aptidao` | `TODO(Guilherme)` |
| Ricardo, Ricardo Irineu | `docs/tarefas/ricardo.md` | `feat/ricardo-campanhas` | `TODO(Ricardo)` |
| Amon, Amon Butk | `docs/tarefas/amon.md` | `feat/amon-doacoes` | `TODO(Amon)` |
| Maria, Maria Vitória, Vitória | `docs/tarefas/maria-vitoria.md` | `feat/maria-frontend` | `TODO(Maria)` |

Se o nome não estiver claro, **pergunte** antes de começar.

### Roteiro obrigatório

1. **Preparar o Git**
   - Rode `git status`. Se houver alterações não commitadas, **pare e pergunte** ao usuário o que fazer.
   - `git checkout main` e depois `git pull origin main`.
   - Se a branch da tarefa ainda não existe: `git checkout -b <branch>`.
     Se já existe (continuação): `git checkout <branch>` e depois `git merge main`.
2. **Ler antes de codar**
   - O arquivo da tarefa inteiro (`docs/tarefas/<nome>.md`).
   - `docs/CONTRATOS.md`.
   - Todos os arquivos `.java` citados na tarefa, incluindo os Javadocs (eles descrevem o comportamento esperado).
3. **Implementar somente o que é do integrante**
   - Completar os métodos marcados com `TODO(<Nome>)`, apagando o `throw new UnsupportedOperationException(...)` e o comentário `TODO`.
   - Criar os arquivos novos listados na tarefa.
   - Ao final, **não pode sobrar nenhum `TODO(<Nome>)`** no projeto (confira com uma busca).
4. **Proibido** (se for impossível cumprir a tarefa sem isso, **pare e explique ao usuário** o que precisa ser combinado com o colega):
   - alterar assinaturas públicas/protegidas que já existem (nomes, parâmetros, retornos, `throws`);
   - editar arquivos ou `TODO`s de **outro** integrante;
   - alterar o schema do banco (`Database.java`, só o Gabriel) ou `Main.java` (só a Maria Vitória);
   - adicionar bibliotecas/frameworks (Spring, Lombok, Gson etc.); o projeto é Java puro;
   - fazer push na `main` ou usar `git push --force`.
5. **Compilar**
   - Windows: `build.bat compilar` · Linux/macOS: `./build.sh compilar`.
   - Corrija até aparecer `Compilacao OK.`
6. **Testar**
   - Crie e rode a classe de teste indicada na tarefa: `build.bat com.doamais.teste.<Classe>`.
   - Se o teste falhar com `UnsupportedOperationException: TODO(<OutroNome>)`, isso é **esperado**: a parte do colega ainda não foi feita. **Não implemente a parte dele.** Informe ao usuário o que ficou pendente e de quem depende.
7. **Commit e push**
   - Commits pequenos, em português, no padrão `feat(<modulo>): descrição` (ex.: `feat(doadores): implementa DoadorDAO`).
   - `git push -u origin <branch>`.
8. **Responder ao usuário** com:
   - resumo do que foi implementado (arquivos e métodos);
   - **conceitos de POO do edital** aplicados e onde estão (o aluno precisa explicar na apresentação);
   - resultado da compilação e dos testes, e o que depende de colegas;
   - link para abrir o Pull Request: `https://github.com/G-Ridolfi/DoaMais/compare/main...<branch>?expand=1`.

---

## 2. Stack e arquitetura

- **Java 17+ puro**, servidor HTTP nativo `com.sun.net.httpserver`, **SQLite via JDBC** (`lib/sqlite-jdbc`), front em **HTML/CSS/JS** gerado pelos handlers + arquivos em `web/`.
- Camadas (pacote `com.doamais`): `model` → `dao` → `service` → `handler`, com `exception`, `util`, `db` e `teste`.
- Detalhes, rotas, campos de formulário e schema: **`docs/CONTRATOS.md`**.

## 3. Requisitos do edital de LPOO (todos precisam aparecer no código)

Herança · Polimorfismo · Tratamento de exceção · Coleções · Leitura e gravação (banco de dados SQLite) · Interface gráfica.
Cada tarefa indica quais conceitos aquele integrante deve demonstrar. **Implemente de forma que o conceito fique evidente**: use `@Override`, comente onde há polimorfismo e use os tipos de coleção pedidos.

## 4. Padrões de código (vale nota de legibilidade)

- Javadoc em toda classe e método público; comentários em português.
- Atributos `private` (ou `protected` em superclasses) com getters/setters.
- **Todo SQL fica nos DAOs**, sempre com `PreparedStatement` e try-with-resources. Nunca concatenar valores do usuário em SQL.
- Exceções: DAO lança `DAOException`; model/service lançam `ValidacaoException`/`DoadorInaptoException`; **só o `BaseHandler` mostra erro ao usuário**. Nada de `catch` vazio nem `e.printStackTrace()` fora do `BaseHandler`.
- No Java use os enums (`TipoSanguineo`, `Sexo`, `StatusCampanha`, `NivelEstoque`), nunca `String` solta. Datas com `LocalDate`.
- Todo texto vindo do usuário/banco que vai para o HTML passa por `HtmlUtil.escapar()`.
- Formulários: converter campos sempre com `FormUtil.getTexto/getInt/getIntOpcional/getDouble/getData`.
- Indentação de 4 espaços, nomes em português, sem acentos em identificadores.
- Não commitar `out/` nem `database/*.db` (já estão no `.gitignore`).
