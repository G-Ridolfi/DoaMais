package com.doamais.service;

import com.doamais.dao.DoacaoDAO;
import com.doamais.dao.DoadorDAO;
import com.doamais.dao.ReceptorDAO;
import com.doamais.exception.DoaMaisException;
import com.doamais.exception.ValidacaoException;
import com.doamais.model.Doacao;
import com.doamais.model.Doador;
import com.doamais.model.Receptor;

import java.time.LocalDate;

/**
 * Fluxo completo de REGISTRO DE DOACAO (o "gran finale" da demo).
 * Integra os modulos de Pedro, Guilherme, Ricardo e Gabriel.
 *
 * Passos de registrar():
 * <ol>
 *   <li>buscar o doador (DoadorDAO.buscarPorId) - se nao existir: ValidacaoException;</li>
 *   <li>se receptorId != null, buscar o receptor (ReceptorDAO.buscarPorId) - se nao existir: ValidacaoException;</li>
 *   <li>motorAptidao.verificarOuLancar(doador) - pode lancar DoadorInaptoException;</li>
 *   <li>se direcionada: receptor.registrarDoacao() - lanca ValidacaoException se a campanha ja estiver concluida;</li>
 *   <li>salvar a doacao (DoacaoDAO.inserir);</li>
 *   <li>DoadorDAO.atualizarUltimaDoacao(doadorId, data);</li>
 *   <li>se direcionada: ReceptorDAO.atualizarProgresso(receptor).</li>
 * </ol>
 * As excecoes NAO sao tratadas aqui: sobem ate o BaseHandler, que mostra a
 * mensagem na tela.
 *
 * Responsavel: Amon.
 * CONTRATO: usado pelo DoacaoHandler.
 */
public class DoacaoService {

    // COMPOSICAO: o service "tem" os DAOs e o motor, criados no construtor
    private final DoadorDAO doadorDAO;
    private final ReceptorDAO receptorDAO;
    private final DoacaoDAO doacaoDAO;
    private final MotorAptidao motor;

    public DoacaoService() {
        this.doadorDAO = new DoadorDAO();
        this.receptorDAO = new ReceptorDAO();
        this.doacaoDAO = new DoacaoDAO();
        this.motor = new MotorAptidao();
    }

    /**
     * @param doadorId   id do doador
     * @param receptorId id da campanha, ou null para o estoque geral
     * @param data       data da doacao
     * @param local      local da doacao (opcional)
     * @return a doacao registrada (com id preenchido)
     */
    public Doacao registrar(int doadorId, Integer receptorId, LocalDate data, String local)
            throws DoaMaisException {
        // Validacao simples da data antes de mexer no banco
        if (data == null) {
            throw new ValidacaoException("A data da doação é obrigatória.");
        }
        if (data.isAfter(LocalDate.now())) {
            throw new ValidacaoException("A data da doação não pode estar no futuro.");
        }

        // PROPAGACAO DE EXCECOES: nenhum try/catch neste metodo. Qualquer
        // ValidacaoException, DAOException ou DoadorInaptoException "sobe" pela
        // clausula throws ate o BaseHandler, que a transforma em alerta na tela.

        // 1. buscar o doador
        Doador doador = doadorDAO.buscarPorId(doadorId)
                .orElseThrow(() -> new ValidacaoException("Doador não encontrado"));

        // 2. se for direcionada, buscar a campanha
        Receptor receptor = null; // null = estoque geral
        if (receptorId != null) {
            receptor = receptorDAO.buscarPorId(receptorId)
                    .orElseThrow(() -> new ValidacaoException("Campanha não encontrada"));
        }

        // 3. regras de aptidao (pode lancar DoadorInaptoException)
        motor.verificarOuLancar(doador);

        // 4. soma 1 na campanha (ENCAPSULAMENTO: so o Receptor altera o proprio progresso)
        if (receptor != null) {
            receptor.registrarDoacao();
        }

        // 5. salvar a doacao (ASSOCIACAO: a Doacao guarda os objetos Doador e Receptor)
        Doacao doacao = new Doacao(0, doador, receptor, data, local);
        doacaoDAO.inserir(doacao);

        // 6. a ultima doacao do doador passa a ser esta data
        doadorDAO.atualizarUltimaDoacao(doadorId, data);

        // 7. grava o novo progresso da campanha
        if (receptor != null) {
            receptorDAO.atualizarProgresso(receptor);
        }

        return doacao;
    }
}