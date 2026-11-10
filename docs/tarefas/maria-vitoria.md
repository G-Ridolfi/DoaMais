# Tarefa: Maria Vitória Poubel de Paula Pires · Frontend, servidor e integração geral

- **Branch:** `feat/maria-frontend`
- **Marcador no código:** `TODO(Maria)`
- **Prioridade:** 🔴 **alta, fazer cedo.** `BaseHandler`, `FormUtil` e `HtmlUtil` são usados por **todas** as telas.
- **Conceitos do edital que você demonstra:** **interface gráfica** (principal), **herança**, **polimorfismo** (Template Method), **tratamento de exceção**, coleções.

## Contexto

Você constrói a **interface web** do sistema e a infraestrutura das telas: a classe base dos handlers, o parser de formulários **feito à mão** (desafio técnico dos slides), os componentes HTML reutilizáveis, a **home** (termômetro do HEMOES + feed de campanhas), o CSS responsivo, o JavaScript e o README final. O `Main` já sobe o servidor e registra as rotas de todos; falta você servir os arquivos estáticos.

**Identidade visual:** vinho `#7a1020` como cor principal, fundo claro, cards com sombra suave, ícone de gota de sangue (pode ser emoji 🩸 ou SVG inline), fonte sem serifa do sistema.

## O que fazer

### 1. `src/com/doamais/handler/BaseHandler.java`: completar os `TODO(Maria)`
Siga o Javadoc da classe:
- `handle(HttpExchange ex)` (**Template Method / polimorfismo**: chama o `get`/`post` sobrescrito por cada filho):
  ```
  try {
      switch (método) { GET → get(ex); POST → post(ex, FormUtil.parse(FormUtil.lerCorpo(ex))); outro → 405 }
  } catch (DoaMaisException e) {   // ValidacaoException, DAOException, DoadorInaptoException...
      página 400: HtmlUtil.pagina("Atenção", HtmlUtil.alerta("erro", e.getMessage()) + botão Voltar (history.back()))
  } catch (Exception e) {
      e.printStackTrace(); página 500 genérica ("Ops! Algo deu errado.")
  } finally {
      ex.close();
  }
  ```
  Se a resposta já tiver sido enviada antes do erro (`ex.getResponseCode() != -1`), não tente enviar outra.
- `post(...)` padrão → página 405.
- `enviarResposta(...)`: `Content-Type`, `sendResponseHeaders(status, corpo.length)`, escrever no `getResponseBody()` com try-with-resources.
- `redirecionar(...)`: header `Location` + `sendResponseHeaders(303, -1)`.
- `getParametro(...)`: `FormUtil.parseQuery(ex.getRequestURI()).get(nome)`.

### 2. `src/com/doamais/util/FormUtil.java`: completar os `TODO(Maria)`
- `lerCorpo`: `new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8)`.
- `parse(String dados)` → `LinkedHashMap`: dividir por `&`, depois pelo **primeiro** `=`; `URLDecoder.decode(x, StandardCharsets.UTF_8)` na chave e no valor; ignorar pares vazios; `null`/vazio → mapa vazio. **Implemente na mão** (é o "desafio técnico" dos slides).
- `parseQuery(URI)` → `parse(uri.getRawQuery())`.
- `getTexto(form, campo, obrigatorio)`: `trim()`; vazio → `null` ou `ValidacaoException("O campo '" + campo + "' é obrigatório.")`.
- `getInt` / `getIntOpcional` / `getDouble` (aceita vírgula → troca por ponto) / `getData` (`LocalDate.parse`): erro de conversão → `ValidacaoException("O campo 'x' ...")` com a exceção original como causa (`NumberFormatException`, `DateTimeParseException`).
- Crie um método privado auxiliar para nomes amigáveis dos campos, se quiser (ex.: `dataNascimento` → "data de nascimento").

### 3. `src/com/doamais/util/HtmlUtil.java`: completar os `TODO(Maria)`
Cada método está descrito no Javadoc. Detalhes:
- `escapar`: `&` primeiro, depois `<`, `>`, `"`, `'`.
- `pagina(titulo, conteudo)`: `<!DOCTYPE html>`, `lang="pt-BR"`, `meta viewport`, `<link rel="stylesheet" href="/static/style.css">`, `<script src="/static/app.js" defer>`, cabeçalho com logo "🩸 DoaMais" e o **menu** (Início, Doadores, Pré-triagem, Nova campanha, Doações, Exportar), `<main>` com `<h1>` + conteúdo, rodapé ("DoaMais – Projeto Integrador II / LPOO · ODS 3 Saúde e Bem-Estar").
- `alerta(tipo, msg)`: `<div class="alerta alerta-<tipo>">`.
- `barraProgresso(p)`: `<div class="progresso"><div class="progresso-barra" style="width:NN%"></div><span>NN%</span></div>` (arredondar).
- `cardCampanha(Receptor r)`: nome, hospital, badge do tipo, "X de Y bolsas", barra, links "Ver campanha" e **"Quero doar"** (ver Javadoc). Usa `r.getProgresso()` (Ricardo).
- `badgeEstoque(tipo, nivel)`: badge com `background` = `nivel.getCor()`, tipo em destaque e `nivel.getDescricao()` embaixo; classe extra `critico` para animar/pulsar no CSS.
- `formatarData`: `dd/MM/yyyy`; `null` → `"—"`.
- `opcoesTipoSanguineo(sel)`: percorrer `TipoSanguineo.values()`.

### 4. `src/com/doamais/handler/HomeHandler.java`: completar o `TODO(Maria)`
- Caminho diferente de `/` → `enviarHtml(ex, 404, pagina "Página não encontrada")`.
- **Termômetro do HEMOES** (topo): título "Estoque do HEMOES hoje", os 8 badges (`new EstoqueDAO().carregarTermometro()`, percorrendo o `EnumMap` com `forEach`/`entrySet`), e uma frase de alerta listando os tipos **críticos** (ex.: "Urgente: O- e B- em nível crítico").
- **Feed de campanhas ativas**: `new ReceptorDAO().listarAtivas()` → grade de `HtmlUtil.cardCampanha(r)`. Lista vazia → mensagem "Nenhuma campanha ativa no momento".
- Atalhos: "Posso doar? Faça a pré-triagem" (`/aptidao`) e "Criar campanha" (`/campanhas/nova`).

### 5. `src/com/doamais/Main.java`: completar o `TODO(Maria)`
Contexto `/static/` servindo a pasta `web/`:
- caminho contendo `..` → 404 (segurança);
- `Content-Type` por extensão (`.css` → `text/css; charset=UTF-8`, `.js` → `application/javascript; charset=UTF-8`, `.svg`, `.png`);
- arquivo inexistente → 404.
- Pode ser uma classe nova `src/com/doamais/handler/ArquivoEstaticoHandler.java` (implements `HttpHandler`; não precisa herdar `BaseHandler` porque envia bytes, não HTML).

### 6. Criar `web/style.css`
- Layout responsivo (mobile first, `max-width` ~1100px centralizado, menu que quebra linha no celular);
- classes usadas pelo `HtmlUtil`: `.alerta-sucesso/erro/aviso/info`, `.progresso`, `.progresso-barra`, `.card`, `.grade-cards` (CSS grid `auto-fill, minmax(260px,1fr)`), `.badge-estoque`, `.critico` (animação pulsando), `.termometro` (grid de 8);
- formulários (labels em cima, inputs 100%, botão vinho), tabelas com zebra.

### 7. Criar `web/app.js`
- **Validação client-side**: campos `required`, CPF com 11 dígitos, peso > 0, e impedir data futura em `input[type=date]` (setar `max` = hoje).
- **Cálculo instantâneo de aptidão** no formulário de `/aptidao`: ao mudar os campos, mostrar uma prévia ("Parece que você está apto" / "Atenção: ...") com as mesmas regras (18–69 anos, ≥ 50 kg, 60/90 dias). O servidor continua sendo a fonte oficial (o Guilherme valida no Java).
- Confirmação (`confirm`) antes de enviar o formulário de doação.

### 8. Atualizar o `README.md`
Ao final do projeto: prints das telas (pasta `docs/img/`), como rodar, funcionalidades e onde cada conceito de LPOO aparece (tabela da seção "Conceitos" do README).

### 9. Teste: `src/com/doamais/teste/FormUtilTeste.java` (criar)
`main` que imprime:
- `FormUtil.parse("nome=Jo%C3%A3o+Silva&peso=72%2C5&vazio=")` → `{nome=João Silva, peso=72,5, vazio=}`;
- `getDouble` com `"72,5"` → 72.5; `getInt` com `"abc"` → mensagem da `ValidacaoException`; `getTexto` obrigatório vazio → mensagem;
- `HtmlUtil.escapar("<script>alert('x')</script>")`;
- `HtmlUtil.barraProgresso(60)` e `HtmlUtil.formatarData(LocalDate.of(2026,11,23))`.

Rodar: `build.bat com.doamais.teste.FormUtilTeste`. Depois, `build.bat` e abra http://localhost:8080. Com o Gabriel na `main`, a home deve mostrar o termômetro. O feed depende do Ricardo.

## Não mexa em
DAOs, models e services dos colegas. As rotas do `Main` já estão definidas; não mude os prefixos (todos dependem deles).

## Critérios de pronto
- [ ] Nenhum `TODO(Maria)` restante; `build.bat compilar` OK; `FormUtilTeste` OK.
- [ ] Servidor sobe com `build.bat`; `/static/style.css` carrega; menu navega entre todas as telas.
- [ ] Um erro de validação em qualquer formulário aparece como **alerta** na página (não como página quebrada) e o servidor continua de pé.
- [ ] Layout funciona no celular (teste com DevTools → modo responsivo).

## Na apresentação, saiba explicar
- Como funciona o **servidor HTTP nativo** do Java (`HttpServer`, contextos, `HttpExchange`, threads).
- Como a `BaseHandler` usa **herança** e **polimorfismo** (Template Method) para evitar código repetido nos 6 handlers.
- Como o `FormUtil` lê um **POST na mão** (URL encoding, UTF-8, `Map<String,String>`).
- Onde as **exceções** são tratadas e por que o servidor nunca cai.
- Por que escapar HTML (**XSS**) e por que validar no cliente **e** no servidor.
