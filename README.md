# 🩸 DoaMais

Plataforma comunitária de **mobilização e apoio à doação de sangue**: campanhas de reposição para pacientes internados, pré-triagem de aptidão (critérios do Ministério da Saúde) e termômetro do estoque do HEMOES.

Projeto integrado das disciplinas **Projeto Integrador II** e **Linguagem de Programação Orientada a Objetos (LPOO)**.
**Stack:** Java 17+ puro (`com.sun.net.httpserver`) · SQLite via JDBC · HTML/CSS/JS.

---

## 🚀 Como cada integrante trabalha

1. **Uma vez só:** aceite o convite de colaborador no GitHub, instale **Git** e **JDK 17+**, e clone:
   ```
   git clone https://github.com/G-Ridolfi/DoaMais.git
   ```
2. Abra a pasta `DoaMais` como workspace no Antigravity.
3. Peça no chat:
   > **Faça a tarefa do &lt;seu nome&gt;.**

   Para mais garantia, use o **prompt pronto** do seu nome em [`docs/PROMPTS.md`](docs/PROMPTS.md). Lá também há prompts para atualizar sua parte depois que os colegas subirem e para estudar para a apresentação.

O Antigravity lê as regras em [`.agent/rules/padrao-do-grupo.md`](.agent/rules/padrao-do-grupo.md) e segue o roteiro: atualiza a `main`, cria sua branch, lê **sua tarefa** em [`docs/tarefas/`](docs/tarefas/), implementa só os `TODO(<seu nome>)`, compila, testa, faz commit e push, e te devolve o link do Pull Request.

4. Abra o **Pull Request** pelo link, revise e faça o merge na `main`. Avise o grupo.
5. **Leia o resumo** que o Antigravity der e o arquivo da sua tarefa. Na apresentação **cada um explica a própria parte** (a nota é individual).

| Integrante | Tarefa | Módulo |
|---|---|---|
| Gabriel Ridolfi Pupim | [gabriel.md](docs/tarefas/gabriel.md) | Dados, persistência (SQLite + CSV) e exceções |
| Pedro Paulo Rodrigues da Silva | [pedro.md](docs/tarefas/pedro.md) | Doadores |
| Guilherme Caiado Richa | [guilherme.md](docs/tarefas/guilherme.md) | Motor de regras de aptidão |
| Ricardo Irineu Sales Machado | [ricardo.md](docs/tarefas/ricardo.md) | Campanhas e receptores |
| Amon Butk Rangel Alves | [amon.md](docs/tarefas/amon.md) | Doações, vínculo e estoque |
| Maria Vitória Poubel de Paula Pires | [maria-vitoria.md](docs/tarefas/maria-vitoria.md) | Frontend, servidor e integração |

> **Ordem sugerida:** Gabriel e Maria Vitória primeiro (base dos DAOs e das telas), depois Pedro, Ricardo e Guilherme, e por último Amon (integra tudo). O código **compila desde já**, então todos podem trabalhar em paralelo. Veja [`docs/CONTRATOS.md`](docs/CONTRATOS.md) §6.

---

## ▶️ Como rodar

```
build.bat                                  # Windows: compila e sobe em http://localhost:8080
./build.sh                                 # Linux/macOS
build.bat compilar                         # só compila
build.bat com.doamais.teste.DatabaseTeste  # roda uma classe de teste
```

O banco `database/doamais.db` é criado automaticamente (não vai para o Git). Para recomeçar do zero, apague esse arquivo.

> Enquanto houver partes pendentes, as telas correspondentes dão erro (`UnsupportedOperationException: TODO(<Nome>)`). Isso é esperado.

---

## 🗂️ Estrutura

```
src/com/doamais/
  Main.java        servidor e rotas
  db/              Database (conexão + tabelas), CsvUtil (arquivos CSV)
  exception/       DoaMaisException e subclasses
  model/           Pessoa (abstrata), Doador, Receptor, Doacao, enums
  dao/             BaseDAO<T> (abstrata genérica) e DAOs
  service/         RegraAptidao (interface), regras, MotorAptidao, DoacaoService
  handler/         BaseHandler (abstrata) e telas
  util/            FormUtil, HtmlUtil
  teste/           classes de teste (uma por integrante)
web/               style.css, app.js
dados/seed/        dados da demonstração (CSV)
docs/              CONTRATOS.md e tarefas/
lib/               driver SQLite JDBC
```

---

## 🎓 Conceitos de LPOO no código (edital 4HC1)

| Conceito | Onde |
|---|---|
| **Herança** | `Doador`/`Receptor` → `Pessoa` · DAOs → `BaseDAO<T>` · handlers → `BaseHandler` · exceções → `DoaMaisException` |
| **Polimorfismo** | `List<RegraAptidao>` no `MotorAptidao` · `getDescricao()`/`validar()` sobrescritos · `mapear()` nos DAOs · `get()`/`post()` nos handlers |
| **Tratamento de exceção** | `DAOException`, `ValidacaoException`, `DoadorInaptoException` · try-with-resources · tratamento central no `BaseHandler` |
| **Coleções** | `List` nos DAOs · `EnumMap` no termômetro · `Map` nas estatísticas e no `FormUtil` |
| **Leitura e gravação** | SQLite via JDBC · exportação/importação CSV (`CsvUtil`, `/exportar`) |
| **Interface gráfica** | Páginas web (HTML/CSS/JS) servidas pelo próprio Java |

---

## 🔀 Regras de Git

- Nunca commitar direto na `main`; cada um trabalha na sua branch (`feat/<nome>-<modulo>`) e abre Pull Request.
- Antes de começar: `git checkout main && git pull`.
- Cada um edita **só os próprios arquivos** (ver `docs/CONTRATOS.md` §1).
