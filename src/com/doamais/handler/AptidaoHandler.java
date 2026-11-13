package com.doamais.handler;

import com.doamais.dao.DoadorDAO;
import com.doamais.exception.DoaMaisException;
import com.doamais.exception.ValidacaoException;
import com.doamais.model.Doador;
import com.doamais.model.Sexo;
import com.doamais.model.TipoSanguineo;
import com.doamais.service.MotorAptidao;
import com.doamais.service.ResultadoAptidao;
import com.doamais.util.FormUtil;
import com.doamais.util.HtmlUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

/**
 * Pre-triagem de aptidao. Prefixo: "/aptidao".
 *
 * Rotas:
 * <ul>
 *   <li>GET  /aptidao            formulario (funciona SEM cadastro): dataNascimento,
 *                                sexo (M/F), peso, dataUltimaDoacao (opcional)</li>
 *   <li>GET  /aptidao?doadorId=3 avalia um doador ja cadastrado (DoadorDAO.buscarPorId)</li>
 *   <li>POST /aptidao            monta um Doador temporario com os campos, chama
 *                                MotorAptidao.avaliar() e mostra o resultado</li>
 * </ul>
 * Resultado: alerta "sucesso" (Apto para doar!) ou "erro" com a lista de
 * motivos e "Liberado a partir de dd/mm/aaaa" quando houver data.
 *
 * Responsavel: Guilherme.
 */
public class AptidaoHandler extends BaseHandler {

    private final MotorAptidao motor = new MotorAptidao();

    @Override
    protected void get(HttpExchange ex) throws DoaMaisException, IOException {
        String caminho = getCaminho(ex);
        if (!"/aptidao".equals(caminho)) {
            enviarHtml(ex, 404, HtmlUtil.pagina("Página não encontrada",
                    HtmlUtil.alerta("erro", "Página não encontrada: " + caminho)
                            + "<p><a class=\"botao\" href=\"/aptidao\">&larr; Ir para pré-triagem</a></p>"));
            return;
        }

        String doadorIdParam = getParametro(ex, "doadorId");
        if (doadorIdParam != null && !doadorIdParam.isBlank()) {
            avaliarDoadorCadastrado(ex);
        } else {
            exibirFormulario(ex);
        }
    }

    @Override
    protected void post(HttpExchange ex, Map<String, String> form) throws DoaMaisException, IOException {
        String caminho = getCaminho(ex);
        if (!"/aptidao".equals(caminho)) {
            enviarHtml(ex, 404, HtmlUtil.pagina("Página não encontrada",
                    HtmlUtil.alerta("erro", "Página não encontrada: " + caminho)
                            + "<p><a class=\"botao\" href=\"/aptidao\">&larr; Ir para pré-triagem</a></p>"));
            return;
        }

        LocalDate dataNascimento = FormUtil.getData(form, "dataNascimento", true);
        Sexo sexo = Sexo.deCodigo(FormUtil.getTexto(form, "sexo", true));
        double peso = FormUtil.getDouble(form, "peso");
        LocalDate dataUltimaDoacao = FormUtil.getData(form, "dataUltimaDoacao", false);

        Doador doador = new Doador();
        doador.setNome("Visitante");
        doador.setTipoSanguineo(TipoSanguineo.O_POS);
        doador.setDataNascimento(dataNascimento);
        doador.setSexo(sexo);
        doador.setPeso(peso);
        doador.setDataUltimaDoacao(dataUltimaDoacao);

        ResultadoAptidao resultado = motor.avaliar(doador);
        enviarHtml(ex, HtmlUtil.pagina("Resultado da Pré-triagem", renderizarResultado(resultado)));
    }

    /**
     * Avalia diretamente um doador ja cadastrado no banco a partir do parametro doadorId.
     */
    private void avaliarDoadorCadastrado(HttpExchange ex) throws DoaMaisException, IOException {
        Map<String, String> query = FormUtil.parseQuery(ex.getRequestURI());
        int id = FormUtil.getInt(query, "doadorId");

        Optional<Doador> opt = new DoadorDAO().buscarPorId(id);
        if (opt.isEmpty()) {
            throw new ValidacaoException("Doador não encontrado.");
        }

        Doador doador = opt.get();
        ResultadoAptidao resultado = motor.avaliar(doador);

        String cabecalhoDoador = "<div class=\"card\" style=\"margin-bottom:1rem;\">\n"
                + "  <p><strong>Doador:</strong> " + HtmlUtil.escapar(doador.getDescricao()) + "</p>\n"
                + "  <p><strong>Peso:</strong> " + doador.getPeso() + " kg · "
                + "<strong>Sexo:</strong> " + HtmlUtil.escapar(doador.getSexo().getDescricao()) + " · "
                + "<strong>Última doação:</strong> " + HtmlUtil.formatarData(doador.getDataUltimaDoacao()) + "</p>\n"
                + "</div>\n";

        enviarHtml(ex, HtmlUtil.pagina("Pré-triagem: " + doador.getNome(),
                cabecalhoDoador + renderizarResultado(resultado)));
    }

    /**
     * Exibe o formulario de pre-triagem (funciona sem necessidade de cadastro)
     * com um resumo dos criterios do Ministerio da Saude.
     */
    private void exibirFormulario(HttpExchange ex) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"card\" style=\"margin-bottom:1.25rem;\">\n")
            .append("  <p>Verifique rapidamente se você atende aos critérios básicos do ")
            .append("<strong>Ministério da Saúde</strong> para doação de sangue (não é necessário cadastro):</p>\n")
            .append("  <ul>\n")
            .append("    <li><strong>Idade:</strong> entre 18 e 69 anos completos;</li>\n")
            .append("    <li><strong>Peso:</strong> mínimo de 50 kg;</li>\n")
            .append("    <li><strong>Intervalo entre doações:</strong> mínimo de 60 dias para homens e 90 dias para mulheres.</li>\n")
            .append("  </ul>\n")
            .append("</div>\n")
            .append("<form method=\"post\" action=\"/aptidao\">\n")
            .append("  <label for=\"dataNascimento\">Data de nascimento *</label>\n")
            .append("  <input type=\"date\" id=\"dataNascimento\" name=\"dataNascimento\" required>\n")
            .append("  <label for=\"sexo\">Sexo biológico *</label>\n")
            .append("  <select id=\"sexo\" name=\"sexo\" required>\n")
            .append("    <option value=\"\">Selecione...</option>\n")
            .append("    <option value=\"M\">Masculino</option>\n")
            .append("    <option value=\"F\">Feminino</option>\n")
            .append("  </select>\n")
            .append("  <label for=\"peso\">Peso (kg) *</label>\n")
            .append("  <input type=\"number\" step=\"0.1\" id=\"peso\" name=\"peso\" placeholder=\"Ex: 70.5\" required>\n")
            .append("  <label for=\"dataUltimaDoacao\">Data da última doação (deixe em branco se nunca doou)</label>\n")
            .append("  <input type=\"date\" id=\"dataUltimaDoacao\" name=\"dataUltimaDoacao\">\n")
            .append("  <div style=\"margin-top:1.25rem;\">\n")
            .append("    <button type=\"submit\" class=\"botao\">Verificar aptidão</button>\n")
            .append("    <a class=\"botao botao-secundario\" href=\"/\" style=\"margin-left:0.5rem;\">Voltar ao início</a>\n")
            .append("  </div>\n")
            .append("</form>\n");

        enviarHtml(ex, HtmlUtil.pagina("Pré-triagem de Aptidão", html.toString()));
    }

    /**
     * Renderiza o HTML do resultado de aptidao (alerta de sucesso ou erro com lista de motivos e data).
     */
    private String renderizarResultado(ResultadoAptidao r) {
        StringBuilder html = new StringBuilder();

        if (r.isApto()) {
            html.append(HtmlUtil.alerta("sucesso",
                    "Você está apto para doar! Compareça ao hemocentro com um documento oficial com foto."))
                .append("\n")
                .append("<div style=\"margin-top:1.25rem; display:flex; gap:0.5rem; flex-wrap:wrap;\">\n")
                .append("  <a class=\"botao\" href=\"/\">Ver campanhas na página inicial</a>\n")
                .append("  <a class=\"botao botao-secundario\" href=\"/aptidao\">Nova consulta</a>\n")
                .append("</div>\n");
        } else {
            html.append(HtmlUtil.alerta("erro", "Você não está apto para doar no momento."))
                .append("\n")
                .append("<div class=\"card\">\n")
                .append("  <h3>Motivos de inaptidão</h3>\n")
                .append("  <ul>\n");
            for (String motivo : r.getMotivos()) {
                html.append("    <li>").append(HtmlUtil.escapar(motivo)).append("</li>\n");
            }
            html.append("  </ul>\n");
            if (r.getDataLiberacao() != null) {
                html.append("  <p><strong>Liberado para doar a partir de ")
                    .append(HtmlUtil.formatarData(r.getDataLiberacao()))
                    .append("</strong></p>\n");
            }
            html.append("</div>\n")
                .append("<div style=\"margin-top:1.25rem; display:flex; gap:0.5rem; flex-wrap:wrap;\">\n")
                .append("  <a class=\"botao\" href=\"/aptidao\">Nova pré-triagem</a>\n")
                .append("  <a class=\"botao botao-secundario\" href=\"/\">Voltar para o início</a>\n")
                .append("</div>\n");
        }

        return html.toString();
    }
}
