package com.doamais.handler;

import com.doamais.dao.ReceptorDAO;
import com.doamais.exception.DoaMaisException;
import com.doamais.exception.ValidacaoException;
import com.doamais.model.Receptor;
import com.doamais.model.TipoSanguineo;
import com.doamais.util.FormUtil;
import com.doamais.util.HtmlUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Telas de Campanhas (Receptores). Prefixo: "/campanhas".
 *
 * Rotas:
 *   GET  /campanhas/nova    formulario: nomePaciente, hospital, tipoSanguineo,
 *                             metaDoacoes, historia
 *   POST /campanhas/nova    cria o Receptor (construtor de campanha nova),
 *                             validar(), ReceptorDAO.inserir, redireciona para
 *                             /campanhas/ver?id=NOVO_ID
 *   GET  /campanhas/ver?id= pagina da campanha: historia, hospital, tipo,
 *                             barra de progresso, "faltam X bolsas", status, e
 *                             botao "Quero doar" (/doacoes/nova?receptorId=ID)
 *                             (oculto se concluida)
 * O FEED de campanhas fica na home (HomeHandler, da Maria).
 *
 * Responsavel: Ricardo.
 */
public class ReceptorHandler extends BaseHandler {

    private final ReceptorDAO receptorDAO = new ReceptorDAO();

    @Override
    protected void get(HttpExchange ex) throws DoaMaisException, IOException {
        String caminho = getCaminho(ex);

        if ("/campanhas/nova".equals(caminho)) {
            exibirFormularioNovo(ex);
        } else if ("/campanhas/ver".equals(caminho)) {
            exibirCampanha(ex);
        } else {
            enviarHtml(ex, 404, HtmlUtil.pagina("Página não encontrada",
                    HtmlUtil.alerta("erro", "Página não encontrada: " + caminho)
                            + "<p><a class=\"botao\" href=\"/\">&larr; Voltar para o início</a></p>"));
        }
    }

    @Override
    protected void post(HttpExchange ex, Map<String, String> form) throws DoaMaisException, IOException {
        String caminho = getCaminho(ex);

        if ("/campanhas/nova".equals(caminho)) {
            String nomePaciente = FormUtil.getTexto(form, "nomePaciente", true);
            String hospital = FormUtil.getTexto(form, "hospital", true);
            String tipoRotulo = FormUtil.getTexto(form, "tipoSanguineo", true);
            TipoSanguineo tipoSanguineo = TipoSanguineo.deRotulo(tipoRotulo);
            int metaDoacoes = FormUtil.getInt(form, "metaDoacoes");
            String historia = FormUtil.getTexto(form, "historia", false);

            Receptor r = new Receptor(nomePaciente, tipoSanguineo, hospital, historia, metaDoacoes);
            r.validar();
            receptorDAO.inserir(r);
            redirecionar(ex, "/campanhas/ver?id=" + r.getId());
        } else {
            enviarHtml(ex, 404, HtmlUtil.pagina("Página não encontrada",
                    HtmlUtil.alerta("erro", "Página não encontrada: " + caminho)
                            + "<p><a class=\"botao\" href=\"/\">&larr; Voltar para o início</a></p>"));
        }
    }

    /**
     * Exibe o formulario de criacao de uma nova campanha.
     */
    private void exibirFormularioNovo(HttpExchange ex) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<form method=\"post\" action=\"/campanhas/nova\">\n")
            .append("  <label for=\"nomePaciente\">Nome do paciente *</label>\n")
            .append("  <input type=\"text\" id=\"nomePaciente\" name=\"nomePaciente\" required>\n")
            .append("  <label for=\"hospital\">Hospital *</label>\n")
            .append("  <input type=\"text\" id=\"hospital\" name=\"hospital\" required>\n")
            .append("  <label for=\"tipoSanguineo\">Tipo sanguíneo *</label>\n")
            .append("  <select id=\"tipoSanguineo\" name=\"tipoSanguineo\" required>\n")
            .append("    <option value=\"\">Selecione...</option>\n")
            .append(HtmlUtil.opcoesTipoSanguineo(null))
            .append("  </select>\n")
            .append("  <label for=\"metaDoacoes\">Meta de doações (bolsas) *</label>\n")
            .append("  <input type=\"number\" id=\"metaDoacoes\" name=\"metaDoacoes\" min=\"1\" max=\"50\" required>\n")
            .append("  <label for=\"historia\">História do paciente</label>\n")
            .append("  <textarea id=\"historia\" name=\"historia\" rows=\"4\" ")
            .append("placeholder=\"Conte a história do paciente para sensibilizar os doadores...\"></textarea>\n")
            .append("  <div style=\"margin-top:1.25rem;\">\n")
            .append("    <button type=\"submit\" class=\"botao\">Criar campanha</button>\n")
            .append("    <a class=\"botao botao-secundario\" href=\"/\" style=\"margin-left:0.5rem;\">Cancelar</a>\n")
            .append("  </div>\n")
            .append("</form>\n");

        enviarHtml(ex, HtmlUtil.pagina("Nova Campanha", html.toString()));
    }

    /**
     * Exibe a pagina de detalhes de uma campanha com progresso, historia,
     * dados e botao "Quero doar" (ou alerta de meta atingida se concluida).
     */
    private void exibirCampanha(HttpExchange ex) throws DoaMaisException, IOException {
        String idStr = getParametro(ex, "id");
        if (idStr == null || idStr.isBlank()) {
            throw new ValidacaoException("Campanha não encontrada");
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new ValidacaoException("Campanha não encontrada", e);
        }

        Optional<Receptor> opt = receptorDAO.buscarPorId(id);
        if (opt.isEmpty()) {
            throw new ValidacaoException("Campanha não encontrada");
        }

        Receptor r = opt.get();
        String tipo = r.getTipoSanguineo() != null ? r.getTipoSanguineo().getRotulo() : "";

        StringBuilder html = new StringBuilder();

        // Informacoes basicas
        html.append("<div class=\"card\">\n")
            .append("  <p><strong>Paciente:</strong> ").append(HtmlUtil.escapar(r.getNome())).append("</p>\n")
            .append("  <p><strong>Hospital:</strong> ").append(HtmlUtil.escapar(r.getHospital())).append("</p>\n")
            .append("  <p><strong>Tipo sanguíneo:</strong> <span class=\"badge-tipo\">")
            .append(HtmlUtil.escapar(tipo)).append("</span></p>\n");

        // Historia (opcional)
        if (r.getHistoria() != null && !r.getHistoria().isBlank()) {
            html.append("  <p><strong>História:</strong> ").append(HtmlUtil.escapar(r.getHistoria())).append("</p>\n");
        }

        html.append("  <p><strong>Data de criação:</strong> ").append(HtmlUtil.formatarData(r.getDataCriacao())).append("</p>\n");

        // Progresso
        html.append("  <p><strong>").append(r.getDoacoesRecebidas()).append(" de ")
            .append(r.getMetaDoacoes()).append(" bolsas</strong></p>\n")
            .append("  ").append(HtmlUtil.barraProgresso(r.getProgresso())).append("\n")
            .append("  <p><strong>Faltam ").append(r.getFaltam()).append(" bolsas</strong></p>\n")
            .append("  <p><strong>Status:</strong> ").append(HtmlUtil.escapar(r.getStatus().getDescricao())).append("</p>\n");

        // Botao ou alerta
        if (r.isConcluida()) {
            html.append("  ").append(HtmlUtil.alerta("sucesso", "Meta atingida! Obrigado a todos os doadores.")).append("\n");
        } else {
            html.append("  <p><a class=\"botao\" href=\"/doacoes/nova?receptorId=").append(r.getId())
                .append("\">🩸 Quero doar</a></p>\n");
        }

        html.append("</div>\n")
            .append("<p><a class=\"botao botao-secundario\" href=\"/\">&larr; Voltar para o início</a></p>\n");

        enviarHtml(ex, HtmlUtil.pagina(HtmlUtil.escapar(r.getNome()), html.toString()));
    }
}
