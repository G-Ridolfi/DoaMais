package com.doamais.handler;

import com.doamais.dao.DoadorDAO;
import com.doamais.exception.DoaMaisException;
import com.doamais.exception.ValidacaoException;
import com.doamais.model.Doador;
import com.doamais.model.Sexo;
import com.doamais.model.TipoSanguineo;
import com.doamais.util.FormUtil;
import com.doamais.util.HtmlUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Telas do modulo de Doadores. Prefixo: "/doadores".
 *
 * Rotas:
 * <ul>
 *   <li>GET  /doadores            lista (tabela) com filtro ?tipo=O- (select de tipo)</li>
 *   <li>GET  /doadores/novo       formulario de cadastro</li>
 *   <li>POST /doadores/novo       cria o Doador, chama validar(), DoadorDAO.inserir,
 *                                 redireciona para /doadores</li>
 *   <li>GET  /doadores/editar?id= formulario preenchido</li>
 *   <li>POST /doadores/editar     atualiza (campo oculto "id"), validar(), DoadorDAO.atualizar</li>
 * </ul>
 * Campos do formulario (atributo name): nome, cpf, dataNascimento, sexo (M/F),
 * peso, tipoSanguineo (A+...), telefone, email, dataUltimaDoacao.
 * Converter com FormUtil.get*(); erros sobem como ValidacaoException.
 *
 * Responsavel: Pedro.
 */
public class DoadorHandler extends BaseHandler {

    private final DoadorDAO doadorDAO = new DoadorDAO();

    @Override
    protected void get(HttpExchange ex) throws DoaMaisException, IOException {
        String caminho = getCaminho(ex);

        if ("/doadores".equals(caminho)) {
            listarDoadores(ex);
        } else if ("/doadores/novo".equals(caminho)) {
            exibirFormularioNovo(ex);
        } else if ("/doadores/editar".equals(caminho)) {
            exibirFormularioEdicao(ex);
        } else {
            enviarHtml(ex, 404, HtmlUtil.pagina("Página não encontrada",
                    HtmlUtil.alerta("erro", "Página não encontrada: " + caminho)
                            + "<p><a class=\"botao\" href=\"/doadores\">&larr; Voltar para doadores</a></p>"));
        }
    }

    @Override
    protected void post(HttpExchange ex, Map<String, String> form) throws DoaMaisException, IOException {
        String caminho = getCaminho(ex);

        if ("/doadores/novo".equals(caminho)) {
            Doador d = lerFormulario(form);
            d.validar();
            doadorDAO.inserir(d);
            redirecionar(ex, "/doadores");
        } else if ("/doadores/editar".equals(caminho)) {
            Doador d = lerFormulario(form);
            int id = FormUtil.getInt(form, "id");
            d.setId(id);
            d.validar();
            doadorDAO.atualizar(d);
            redirecionar(ex, "/doadores");
        } else {
            enviarHtml(ex, 404, HtmlUtil.pagina("Página não encontrada",
                    HtmlUtil.alerta("erro", "Página não encontrada: " + caminho)
                            + "<p><a class=\"botao\" href=\"/doadores\">&larr; Voltar para doadores</a></p>"));
        }
    }

    /**
     * Monta a tela de listagem de doadores com filtro opcional por tipo sanguíneo.
     */
    private void listarDoadores(HttpExchange ex) throws DoaMaisException, IOException {
        String tipoParam = getParametro(ex, "tipo");
        TipoSanguineo tipoFiltro = null;
        List<Doador> doadores;

        if (tipoParam != null && !tipoParam.isBlank()) {
            tipoFiltro = TipoSanguineo.deRotulo(tipoParam);
            doadores = doadorDAO.listarPorTipo(tipoFiltro);
        } else {
            doadores = doadorDAO.listarTodos();
        }

        StringBuilder html = new StringBuilder();

        // Barra de acoes: Novo doador + Filtro
        html.append("<div style=\"display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:1rem; margin-bottom:1.5rem;\">\n")
            .append("  <a class=\"botao\" href=\"/doadores/novo\">+ Novo doador</a>\n")
            .append("  <form method=\"get\" action=\"/doadores\" style=\"box-shadow:none; padding:0; background:none; display:flex; align-items:center; gap:0.5rem; margin:0; max-width:none;\">\n")
            .append("    <label for=\"filtro-tipo\" style=\"margin:0; font-weight:600;\">Filtrar por tipo:</label>\n")
            .append("    <select id=\"filtro-tipo\" name=\"tipo\" style=\"width:auto;\" onchange=\"this.form.submit()\">\n")
            .append("      <option value=\"\">Todos os tipos</option>\n")
            .append(HtmlUtil.opcoesTipoSanguineo(tipoFiltro))
            .append("    </select>\n")
            .append("    <button type=\"submit\" class=\"botao botao-secundario\" style=\"margin:0; width:auto; padding:0.5rem 0.8rem;\">Filtrar</button>\n")
            .append("  </form>\n")
            .append("</div>\n");

        // Tabela de doadores
        html.append("<div class=\"tabela-container\">\n")
            .append("  <table>\n")
            .append("    <thead>\n")
            .append("      <tr>\n")
            .append("        <th>Nome</th>\n")
            .append("        <th>Idade</th>\n")
            .append("        <th>Tipo</th>\n")
            .append("        <th>Sexo</th>\n")
            .append("        <th>Última doação</th>\n")
            .append("        <th>Ações</th>\n")
            .append("      </tr>\n")
            .append("    </thead>\n")
            .append("    <tbody>\n");

        if (doadores.isEmpty()) {
            html.append("      <tr>\n")
                .append("        <td colspan=\"6\" style=\"text-align:center; padding:1.5rem;\">Nenhum doador encontrado.</td>\n")
                .append("      </tr>\n");
        } else {
            for (Doador d : doadores) {
                String rotuloTipo = d.getTipoSanguineo() != null ? d.getTipoSanguineo().getRotulo() : "";
                String descSexo = d.getSexo() != null ? d.getSexo().getDescricao() : "";

                html.append("      <tr>\n")
                    .append("        <td><strong>").append(HtmlUtil.escapar(d.getNome())).append("</strong></td>\n")
                    .append("        <td>").append(d.getIdade()).append(" anos</td>\n")
                    .append("        <td><span class=\"badge-tipo\">").append(HtmlUtil.escapar(rotuloTipo)).append("</span></td>\n")
                    .append("        <td>").append(HtmlUtil.escapar(descSexo)).append("</td>\n")
                    .append("        <td>").append(HtmlUtil.formatarData(d.getDataUltimaDoacao())).append("</td>\n")
                    .append("        <td>\n")
                    .append("          <a class=\"botao botao-secundario\" style=\"padding:0.3rem 0.6rem; font-size:0.85rem;\" href=\"/doadores/editar?id=")
                    .append(d.getId()).append("\">Editar</a>\n")
                    .append("          <a class=\"botao\" style=\"padding:0.3rem 0.6rem; font-size:0.85rem;\" href=\"/aptidao?doadorId=")
                    .append(d.getId()).append("\">Pré-triagem</a>\n")
                    .append("        </td>\n")
                    .append("      </tr>\n");
            }
        }

        html.append("    </tbody>\n")
            .append("  </table>\n")
            .append("</div>\n");

        enviarHtml(ex, HtmlUtil.pagina("Doadores", html.toString()));
    }

    /**
     * Exibe o formulário vazio para criação de um novo doador.
     */
    private void exibirFormularioNovo(HttpExchange ex) throws IOException {
        String html = montarFormulario("Novo Doador", "/doadores/novo", null);
        enviarHtml(ex, HtmlUtil.pagina("Novo Doador", html));
    }

    /**
     * Exibe o formulário preenchido para edição de um doador existente.
     */
    private void exibirFormularioEdicao(HttpExchange ex) throws DoaMaisException, IOException {
        String idStr = getParametro(ex, "id");
        if (idStr == null || idStr.isBlank()) {
            throw new ValidacaoException("Doador não encontrado");
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new ValidacaoException("Doador não encontrado", e);
        }

        Optional<Doador> opt = doadorDAO.buscarPorId(id);
        if (opt.isEmpty()) {
            throw new ValidacaoException("Doador não encontrado");
        }

        Doador d = opt.get();
        String html = montarFormulario("Editar Doador", "/doadores/editar", d);
        enviarHtml(ex, HtmlUtil.pagina("Editar Doador", html));
    }

    /**
     * Monta o HTML do formulário de cadastro ou edição.
     */
    private String montarFormulario(String titulo, String action, Doador doador) {
        boolean edicao = (doador != null);

        String idVal = edicao ? String.valueOf(doador.getId()) : "";
        String nomeVal = edicao ? HtmlUtil.escapar(doador.getNome()) : "";
        String cpfVal = edicao ? HtmlUtil.escapar(doador.getCpf()) : "";
        String dataNascVal = (edicao && doador.getDataNascimento() != null) ? doador.getDataNascimento().toString() : "";
        Sexo sexoVal = edicao ? doador.getSexo() : null;
        String pesoVal = (edicao && doador.getPeso() > 0) ? String.valueOf(doador.getPeso()) : "";
        TipoSanguineo tipoVal = edicao ? doador.getTipoSanguineo() : null;
        String telefoneVal = (edicao && doador.getTelefone() != null) ? HtmlUtil.escapar(doador.getTelefone()) : "";
        String emailVal = (edicao && doador.getEmail() != null) ? HtmlUtil.escapar(doador.getEmail()) : "";
        String dataUltimaVal = (edicao && doador.getDataUltimaDoacao() != null) ? doador.getDataUltimaDoacao().toString() : "";

        StringBuilder html = new StringBuilder();
        html.append("<form method=\"post\" action=\"").append(action).append("\">\n");

        if (edicao) {
            html.append("  <input type=\"hidden\" name=\"id\" value=\"").append(idVal).append("\">\n");
        }

        html.append("  <label for=\"nome\">Nome completo *</label>\n")
            .append("  <input type=\"text\" id=\"nome\" name=\"nome\" value=\"").append(nomeVal).append("\" required>\n")

            .append("  <label for=\"cpf\">CPF (11 dígitos) *</label>\n")
            .append("  <input type=\"text\" id=\"cpf\" name=\"cpf\" value=\"").append(cpfVal)
            .append("\" placeholder=\"Ex: 12345678901 ou 123.456.789-01\" required>\n")

            .append("  <label for=\"dataNascimento\">Data de nascimento *</label>\n")
            .append("  <input type=\"date\" id=\"dataNascimento\" name=\"dataNascimento\" value=\"").append(dataNascVal).append("\" required>\n")

            .append("  <label for=\"sexo\">Sexo biológico *</label>\n")
            .append("  <select id=\"sexo\" name=\"sexo\" required>\n")
            .append("    <option value=\"\">Selecione...</option>\n")
            .append("    <option value=\"M\"").append(sexoVal == Sexo.MASCULINO ? " selected" : "").append(">Masculino</option>\n")
            .append("    <option value=\"F\"").append(sexoVal == Sexo.FEMININO ? " selected" : "").append(">Feminino</option>\n")
            .append("  </select>\n")

            .append("  <label for=\"peso\">Peso (kg) *</label>\n")
            .append("  <input type=\"number\" step=\"0.1\" id=\"peso\" name=\"peso\" value=\"").append(pesoVal).append("\" required>\n")

            .append("  <label for=\"tipoSanguineo\">Tipo sanguíneo *</label>\n")
            .append("  <select id=\"tipoSanguineo\" name=\"tipoSanguineo\" required>\n")
            .append("    <option value=\"\">Selecione...</option>\n")
            .append(HtmlUtil.opcoesTipoSanguineo(tipoVal))
            .append("  </select>\n")

            .append("  <label for=\"telefone\">Telefone</label>\n")
            .append("  <input type=\"tel\" id=\"telefone\" name=\"telefone\" value=\"").append(telefoneVal).append("\" placeholder=\"(27) 99999-9999\">\n")

            .append("  <label for=\"email\">E-mail</label>\n")
            .append("  <input type=\"email\" id=\"email\" name=\"email\" value=\"").append(emailVal).append("\" placeholder=\"exemplo@email.com\">\n")

            .append("  <label for=\"dataUltimaDoacao\">Data da última doação (opcional)</label>\n")
            .append("  <input type=\"date\" id=\"dataUltimaDoacao\" name=\"dataUltimaDoacao\" value=\"").append(dataUltimaVal).append("\">\n")

            .append("  <div style=\"margin-top:1.25rem;\">\n")
            .append("    <button type=\"submit\" class=\"botao\">Salvar doador</button>\n")
            .append("    <a class=\"botao botao-secundario\" href=\"/doadores\" style=\"margin-left:0.5rem;\">Cancelar</a>\n")
            .append("  </div>\n")
            .append("</form>\n");

        return html.toString();
    }

    /**
     * Converte os campos enviados no formulário em uma instância de Doador.
     * Os métodos get* do FormUtil lançam ValidacaoException caso algum campo esteja incorreto.
     */
    private Doador lerFormulario(Map<String, String> form) throws ValidacaoException {
        String nome = FormUtil.getTexto(form, "nome", true);
        String cpf = FormUtil.getTexto(form, "cpf", true);
        LocalDate dataNascimento = FormUtil.getData(form, "dataNascimento", true);
        String sexoCodigo = FormUtil.getTexto(form, "sexo", true);
        Sexo sexo = Sexo.deCodigo(sexoCodigo);
        double peso = FormUtil.getDouble(form, "peso");
        String tipoRotulo = FormUtil.getTexto(form, "tipoSanguineo", true);
        TipoSanguineo tipoSanguineo = TipoSanguineo.deRotulo(tipoRotulo);
        String telefone = FormUtil.getTexto(form, "telefone", false);
        String email = FormUtil.getTexto(form, "email", false);
        LocalDate dataUltimaDoacao = FormUtil.getData(form, "dataUltimaDoacao", false);

        Doador d = new Doador();
        d.setNome(nome);
        d.setCpf(cpf);
        d.setDataNascimento(dataNascimento);
        d.setSexo(sexo);
        d.setPeso(peso);
        d.setTipoSanguineo(tipoSanguineo);
        d.setTelefone(telefone);
        d.setEmail(email);
        d.setDataUltimaDoacao(dataUltimaDoacao);
        return d;
    }
}
