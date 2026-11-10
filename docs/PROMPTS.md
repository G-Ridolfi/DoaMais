# 🤖 Prompts padrão para o Antigravity

Cada integrante copia **o seu prompt** e cola no chat do Antigravity, com a pasta `DoaMais` aberta como workspace.

> **Antes de começar:** Git e JDK 17+ instalados, convite de colaborador aceito, repositório clonado.
> **Uma tarefa por conversa:** abra uma conversa nova no Antigravity para cada tarefa.
> **Modelo:** Claude Sonnet ou Gemini no modo normal servem. Para a tarefa do Amon (que integra tudo), prefira um modelo mais forte (Opus / modo Thinking).

## Ordem sugerida

| Ordem | Integrante | Por quê |
|---|---|---|
| 🟥 1º | Gabriel e Maria Vitória | `BaseDAO`, `BaseHandler`, `FormUtil` e `HtmlUtil` são usados por todos |
| 🟨 2º | Pedro, Ricardo e Guilherme | Doadores, campanhas e aptidão |
| 🟩 3º | Amon | Integra o trabalho de todos no registro de doação |

Todos podem começar em paralelo (o código compila desde já), mas os testes só passam por completo quando as partes de que cada um depende estiverem na `main`.

---

## 🟥 Gabriel

```
Sou o Gabriel Ridolfi, integrante do projeto DoaMais. Faça a minha tarefa.

Antes de escrever código, leia por completo:
- .agent/rules/padrao-do-grupo.md (siga o roteiro da seção 1 à risca)
- docs/tarefas/gabriel.md (minha tarefa)
- docs/CONTRATOS.md
- os arquivos .java citados na tarefa, incluindo os Javadocs

Regras:
- Trabalhe na branch feat/gabriel-dados, criada a partir da main atualizada (git checkout main, git pull).
- Implemente SOMENTE os métodos marcados com TODO(Gabriel) e os arquivos novos pedidos na tarefa, incluindo os CSVs de dados/seed/.
- NÃO altere assinaturas existentes, NÃO edite arquivos de outros integrantes, NÃO adicione bibliotecas, NÃO faça push na main.
- Se algo da tarefa for impossível sem mexer na parte de um colega, pare e me explique.
- Compile com build.bat compilar até dar "Compilacao OK", crie e rode o teste com build.bat com.doamais.teste.PersistenciaTeste.
- Confirme que não sobrou nenhum TODO(Gabriel) no projeto.
- Faça commits pequenos em português (feat(dados): ...) e git push -u origin feat/gabriel-dados.

No final, me responda com:
1. o que foi implementado (arquivos e métodos);
2. quais conceitos de POO do edital (herança, polimorfismo, exceções, coleções, leitura/gravação, interface gráfica) aparecem na minha parte e em que linhas/métodos;
3. o resultado da compilação e do teste, e o que depende de colegas;
4. o link do Pull Request: https://github.com/G-Ridolfi/DoaMais/compare/main...feat/gabriel-dados?expand=1
```

## 🟥 Maria Vitória

```
Sou a Maria Vitória, integrante do projeto DoaMais. Faça a minha tarefa.

Antes de escrever código, leia por completo:
- .agent/rules/padrao-do-grupo.md (siga o roteiro da seção 1 à risca)
- docs/tarefas/maria-vitoria.md (minha tarefa)
- docs/CONTRATOS.md
- os arquivos .java citados na tarefa, incluindo os Javadocs

Regras:
- Trabalhe na branch feat/maria-frontend, criada a partir da main atualizada (git checkout main, git pull).
- Implemente SOMENTE os métodos marcados com TODO(Maria) e os arquivos novos pedidos na tarefa (web/style.css, web/app.js e o handler de arquivos estáticos).
- NÃO altere assinaturas existentes nem os prefixos das rotas do Main, NÃO edite arquivos de outros integrantes, NÃO adicione bibliotecas, NÃO faça push na main.
- Se algo da tarefa for impossível sem mexer na parte de um colega, pare e me explique.
- Compile com build.bat compilar até dar "Compilacao OK", crie e rode o teste com build.bat com.doamais.teste.FormUtilTeste.
- Confirme que não sobrou nenhum TODO(Maria) no projeto.
- Faça commits pequenos em português (feat(frontend): ...) e git push -u origin feat/maria-frontend.

No final, me responda com:
1. o que foi implementado (arquivos e métodos);
2. quais conceitos de POO do edital (herança, polimorfismo, exceções, coleções, leitura/gravação, interface gráfica) aparecem na minha parte e em que linhas/métodos;
3. o resultado da compilação e do teste, e o que depende de colegas;
4. o link do Pull Request: https://github.com/G-Ridolfi/DoaMais/compare/main...feat/maria-frontend?expand=1
```

## 🟨 Pedro

```
Sou o Pedro Paulo, integrante do projeto DoaMais. Faça a minha tarefa.

Antes de escrever código, leia por completo:
- .agent/rules/padrao-do-grupo.md (siga o roteiro da seção 1 à risca)
- docs/tarefas/pedro.md (minha tarefa)
- docs/CONTRATOS.md
- os arquivos .java citados na tarefa, incluindo os Javadocs

Regras:
- Trabalhe na branch feat/pedro-doadores, criada a partir da main atualizada (git checkout main, git pull).
- Implemente SOMENTE os métodos marcados com TODO(Pedro) e os arquivos novos pedidos na tarefa.
- NÃO altere assinaturas existentes (Pessoa, TipoSanguineo e Sexo são contratos do grupo), NÃO edite arquivos de outros integrantes, NÃO adicione bibliotecas, NÃO faça push na main.
- Se algo da tarefa for impossível sem mexer na parte de um colega, pare e me explique.
- Compile com build.bat compilar até dar "Compilacao OK", crie e rode o teste com build.bat com.doamais.teste.DoadorTeste.
- Se o teste falhar com "UnsupportedOperationException: TODO(<outro nome>)", é porque a parte do colega ainda não subiu: NÃO implemente a parte dele, só me avise.
- Confirme que não sobrou nenhum TODO(Pedro) no projeto.
- Faça commits pequenos em português (feat(doadores): ...) e git push -u origin feat/pedro-doadores.

No final, me responda com:
1. o que foi implementado (arquivos e métodos);
2. quais conceitos de POO do edital (herança, polimorfismo, exceções, coleções, leitura/gravação, interface gráfica) aparecem na minha parte e em que linhas/métodos;
3. o resultado da compilação e do teste, e o que depende de colegas;
4. o link do Pull Request: https://github.com/G-Ridolfi/DoaMais/compare/main...feat/pedro-doadores?expand=1
```

## 🟨 Ricardo

```
Sou o Ricardo Irineu, integrante do projeto DoaMais. Faça a minha tarefa.

Antes de escrever código, leia por completo:
- .agent/rules/padrao-do-grupo.md (siga o roteiro da seção 1 à risca)
- docs/tarefas/ricardo.md (minha tarefa)
- docs/CONTRATOS.md
- os arquivos .java citados na tarefa, incluindo os Javadocs

Regras:
- Trabalhe na branch feat/ricardo-campanhas, criada a partir da main atualizada (git checkout main, git pull).
- Implemente SOMENTE os métodos marcados com TODO(Ricardo) e os arquivos novos pedidos na tarefa.
- NÃO altere assinaturas existentes (Amon e Maria dependem de Receptor e ReceptorDAO), NÃO edite arquivos de outros integrantes, NÃO adicione bibliotecas, NÃO faça push na main.
- Se algo da tarefa for impossível sem mexer na parte de um colega, pare e me explique.
- Compile com build.bat compilar até dar "Compilacao OK", crie e rode o teste com build.bat com.doamais.teste.CampanhaTeste.
- Se o teste falhar com "UnsupportedOperationException: TODO(<outro nome>)", é porque a parte do colega ainda não subiu: NÃO implemente a parte dele, só me avise.
- Confirme que não sobrou nenhum TODO(Ricardo) no projeto.
- Faça commits pequenos em português (feat(campanhas): ...) e git push -u origin feat/ricardo-campanhas.

No final, me responda com:
1. o que foi implementado (arquivos e métodos);
2. quais conceitos de POO do edital (herança, polimorfismo, exceções, coleções, leitura/gravação, interface gráfica) aparecem na minha parte e em que linhas/métodos;
3. o resultado da compilação e do teste, e o que depende de colegas;
4. o link do Pull Request: https://github.com/G-Ridolfi/DoaMais/compare/main...feat/ricardo-campanhas?expand=1
```

## 🟨 Guilherme

```
Sou o Guilherme Caiado, integrante do projeto DoaMais. Faça a minha tarefa.

Antes de escrever código, leia por completo:
- .agent/rules/padrao-do-grupo.md (siga o roteiro da seção 1 à risca)
- docs/tarefas/guilherme.md (minha tarefa)
- docs/CONTRATOS.md
- os arquivos .java citados na tarefa, incluindo os Javadocs

Regras:
- Trabalhe na branch feat/guilherme-aptidao, criada a partir da main atualizada (git checkout main, git pull).
- Implemente SOMENTE os métodos marcados com TODO(Guilherme) e crie as classes RegraIdade, RegraPeso e RegraIntervalo, além do teste pedido.
- NÃO altere assinaturas existentes (RegraAptidao, ResultadoAptidao e DoadorInaptoException são contratos), NÃO edite o Doador (é do Pedro) nem outros arquivos de colegas, NÃO adicione bibliotecas, NÃO faça push na main.
- Se algo da tarefa for impossível sem mexer na parte de um colega, pare e me explique.
- Compile com build.bat compilar até dar "Compilacao OK", crie e rode o teste com build.bat com.doamais.teste.AptidaoTeste (usando a data fixa 2026-11-23).
- Se o teste falhar com "UnsupportedOperationException: TODO(Pedro)" ou outro nome, é porque a parte do colega ainda não subiu: NÃO implemente a parte dele, só me avise.
- Confirme que não sobrou nenhum TODO(Guilherme) no projeto.
- Faça commits pequenos em português (feat(aptidao): ...) e git push -u origin feat/guilherme-aptidao.

No final, me responda com:
1. o que foi implementado (arquivos e métodos);
2. quais conceitos de POO do edital (herança, polimorfismo, exceções, coleções, leitura/gravação, interface gráfica) aparecem na minha parte e em que linhas/métodos;
3. o resultado da compilação e do teste, e o que depende de colegas;
4. o link do Pull Request: https://github.com/G-Ridolfi/DoaMais/compare/main...feat/guilherme-aptidao?expand=1
```

## 🟩 Amon

```
Sou o Amon Butk, integrante do projeto DoaMais. Faça a minha tarefa.

Antes de escrever código, leia por completo:
- .agent/rules/padrao-do-grupo.md (siga o roteiro da seção 1 à risca)
- docs/tarefas/amon.md (minha tarefa)
- docs/CONTRATOS.md
- os arquivos .java citados na tarefa, incluindo os Javadocs

Regras:
- Trabalhe na branch feat/amon-doacoes, criada a partir da main atualizada (git checkout main, git pull).
- Implemente SOMENTE os métodos marcados com TODO(Amon) e os arquivos novos pedidos na tarefa.
- Meu módulo usa código de Pedro, Guilherme, Ricardo, Gabriel e Maria: use os métodos deles exatamente como estão declarados; NÃO edite os arquivos deles, NÃO altere assinaturas, NÃO adicione bibliotecas, NÃO faça push na main.
- Se eu precisar de algum método que não existe na parte de um colega, pare e me diga o que pedir a ele.
- Compile com build.bat compilar até dar "Compilacao OK", crie e rode o teste com build.bat com.doamais.teste.DoacaoTeste.
- Se o teste falhar com "UnsupportedOperationException: TODO(<outro nome>)", é porque a parte do colega ainda não subiu: NÃO implemente a parte dele, só me avise quem falta.
- Confirme que não sobrou nenhum TODO(Amon) no projeto.
- Faça commits pequenos em português (feat(doacoes): ...) e git push -u origin feat/amon-doacoes.

No final, me responda com:
1. o que foi implementado (arquivos e métodos);
2. quais conceitos de POO do edital (herança, polimorfismo, exceções, coleções, leitura/gravação, interface gráfica) aparecem na minha parte e em que linhas/métodos;
3. o resultado da compilação e do teste, e o que depende de colegas;
4. o link do Pull Request: https://github.com/G-Ridolfi/DoaMais/compare/main...feat/amon-doacoes?expand=1
```

---

## 🔁 Prompts extras (iguais para todos, troque o que está entre colchetes)

### Atualizar depois que os colegas subirem as partes deles

```
Sou o [NOME]. Meus colegas já subiram as partes deles na main. Atualize minha branch com a main (git checkout [MINHA BRANCH], git merge main), rode de novo meu teste e confira se tudo funciona integrado. Corrija apenas o que for da minha parte; se o problema estiver no código de um colega, me diga o que é e de quem é. Depois faça commit e push.
```

### Estudar para a apresentação (entrevista individual)

```
Sou o [NOME]. Leia docs/tarefas/[ARQUIVO].md e o código que implementei. Me explique, como se eu fosse apresentar para a professora, cada conceito de POO que aparece na minha parte (mostrando o trecho de código), e depois me faça 5 perguntas que ela poderia fazer na entrevista individual, uma de cada vez, corrigindo minhas respostas.
```

| Integrante | `[MINHA BRANCH]` | `[ARQUIVO]` |
|---|---|---|
| Gabriel | `feat/gabriel-dados` | `gabriel` |
| Maria Vitória | `feat/maria-frontend` | `maria-vitoria` |
| Pedro | `feat/pedro-doadores` | `pedro` |
| Ricardo | `feat/ricardo-campanhas` | `ricardo` |
| Guilherme | `feat/guilherme-aptidao` | `guilherme` |
| Amon | `feat/amon-doacoes` | `amon` |

---

## ✅ Antes de aprovar um Pull Request

- Na aba **Files changed** do GitHub, confira se só mudaram arquivos daquele integrante (ver `docs/CONTRATOS.md` §1).
- Confira se o resumo da IA diz **"Compilacao OK"**.
- Depois do merge, avise o grupo para todos rodarem `git pull` na `main`.
