# 🩸 DoaMais

Plataforma comunitária de **mobilização e apoio à doação de sangue**: campanhas de reposição para pacientes internados, pré-triagem de aptidão (critérios do Ministério da Saúde) e termômetro do estoque do HEMOES.

Projeto integrado das disciplinas **Projeto Integrador II** e **Linguagem de Programação Orientada a Objetos (LPOO)**.
**Stack:** Java 17+ puro (`com.sun.net.httpserver`) · SQLite via JDBC · HTML/CSS/JS.

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
  db/              Database (conexão + tabelas + dados iniciais)
  exception/       DoaMaisException e subclasses
  model/           Pessoa (abstrata), Doador, Receptor, Doacao, enums
  dao/             BaseDAO<T> (abstrata genérica) e DAOs
  service/         RegraAptidao (interface), regras, MotorAptidao, DoacaoService
  handler/         BaseHandler (abstrata) e telas
  util/            FormUtil, HtmlUtil
  teste/           classes de teste (uma por integrante)
web/               style.css, app.js
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
| **Leitura e gravação** | Banco de dados relacional SQLite via JDBC (`Database`, `BaseDAO<T>`) |
| **Interface gráfica** | Páginas web (HTML/CSS/JS) servidas pelo próprio Java |

---

## 🔀 Regras de Git

- Nunca commitar direto na `main`; cada um trabalha na sua branch (`feat/<nome>-<modulo>`) e abre Pull Request.
- Antes de começar: `git checkout main && git pull`.
- Cada um edita **só os próprios arquivos** (ver `docs/CONTRATOS.md` §1).
