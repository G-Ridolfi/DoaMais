# 🩸 DoaMais

Plataforma comunitária de **mobilização e apoio à doação de sangue**: campanhas de reposição para pacientes internados, pré-triagem de aptidão (critérios do Ministério da Saúde) e termômetro do estoque do HEMOES.

Projeto integrado das disciplinas **Projeto Integrador II** e **Linguagem de Programação Orientada a Objetos (LPOO)**.  
**Stack:** Java 17+ puro (`com.sun.net.httpserver`) · SQLite via JDBC · HTML/CSS/JS.

---

## 👥 Integrantes e Módulos

| Integrante | Módulo |
|---|---|
| Gabriel Ridolfi Pupim | Infraestrutura de dados, persistência (SQLite/JDBC) e exceções |
| Pedro Paulo Rodrigues da Silva | Cadastro e gerenciamento de doadores |
| Guilherme Caiado Richa | Motor de regras de aptidão (pré-triagem) |
| Ricardo Irineu Sales Machado | Campanhas e receptores |
| Amon Butk Rangel Alves | Registro de doações, vínculo e estoque |
| Maria Vitória Poubel de Paula Pires | Frontend, servidor HTTP e utilitários web |

---

## ▶️ Como rodar

```bat
build.bat                                  # Windows: compila e sobe em http://localhost:8080
./build.sh                                 # Linux/macOS
build.bat compilar                         # só compila
build.bat com.doamais.teste.DatabaseTeste  # roda uma classe de teste
```

O banco `database/doamais.db` é criado e populado automaticamente na primeira execução. Para reiniciar os dados de demonstração do zero, basta apagar esse arquivo.

---

## 🗂️ Estrutura

```
src/com/doamais/
  Main.java        servidor HTTP e registro de rotas
  db/              Database (conexão JDBC + criação de tabelas + dados iniciais)
  exception/       DoaMaisException, DAOException, ValidacaoException, DoadorInaptoException
  model/           Pessoa (abstrata), Doador, Receptor, Doacao e enums
  dao/             BaseDAO<T> (abstrata genérica), DoadorDAO, ReceptorDAO, DoacaoDAO, EstoqueDAO
  service/         RegraAptidao (interface), regras, MotorAptidao, DoacaoService
  handler/         BaseHandler (abstrata) e controladores web
  util/            FormUtil, HtmlUtil
  teste/           classes de teste
web/               style.css, app.js
lib/               driver SQLite JDBC
```

---

## 🎓 Conceitos de LPOO no código (edital 4HC1)

| Conceito | Onde |
|---|---|
| **Herança** | `Doador`/`Receptor` → `Pessoa` · DAOs → `BaseDAO<T>` · handlers → `BaseHandler` · exceções → `DoaMaisException` |
| **Polimorfismo** | `List<RegraAptidao>` no `MotorAptidao` · `getDescricao()`/`validar()` sobrescritos · `mapear()` nos DAOs · `get()`/`post()` nos handlers |
| **Tratamento de exceção** | `DAOException`, `ValidacaoException`, `DoadorInaptoException` · `try-with-resources` · tratamento central no `BaseHandler` |
| **Coleções** | `List` e `Optional` nos DAOs · `EnumMap` no termômetro · `Map` nas estatísticas e no `FormUtil` |
| **Leitura e gravação** | Banco de dados relacional SQLite via JDBC (`Database`, `BaseDAO<T>`) |
| **Interface gráfica** | Páginas web (HTML/CSS/JS) servidas pelo servidor HTTP nativo do Java |
