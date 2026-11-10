package com.doamais.service;

import com.doamais.exception.DoaMaisException;
import com.doamais.model.Doacao;

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

    // TODO(Amon): atributos privados DoadorDAO, ReceptorDAO, DoacaoDAO e MotorAptidao,
    //   criados no construtor

    public DoacaoService() {
        // TODO(Amon): instanciar os DAOs e o motor
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
        // TODO(Amon): implementar os passos descritos no Javadoc da classe
        throw new UnsupportedOperationException("TODO(Amon): DoacaoService.registrar");
    }
}
