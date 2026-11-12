package com.doamais.service;

import com.doamais.exception.DoadorInaptoException;
import com.doamais.model.Doador;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Motor de regras de aptidao (criterios do Ministerio da Saude).
 *
 * POLIMORFISMO: guarda uma List&lt;RegraAptidao&gt; e percorre todas chamando
 * regra.avaliar(...) sem saber qual regra concreta e cada uma.
 *
 * Regras padrao: RegraIdade (18 a 69 anos), RegraPeso (&gt;= 50 kg) e
 * RegraIntervalo (60 dias homens / 90 dias mulheres).
 *
 * Responsavel: Guilherme.
 * CONTRATO: verificarOuLancar e usado pelo Amon (DoacaoService).
 */
public class MotorAptidao {

    private final List<RegraAptidao> regras;

    /** Cria o motor com as 3 regras padrao. */
    public MotorAptidao() {
        this(List.of(new RegraIdade(), new RegraPeso(), new RegraIntervalo()));
    }

    /** Cria o motor com regras especificas (util para testes). */
    public MotorAptidao(List<RegraAptidao> regras) {
        this.regras = new ArrayList<>(regras);
    }

    /**
     * Avalia o doador em TODAS as regras e junta os resultados:
     * apto somente se todas as regras derem apto; motivos = todos os motivos
     * de inaptidao; dataLiberacao = a data MAIS DISTANTE entre as regras
     * (null se nenhuma regra informar data).
     */
    public ResultadoAptidao avaliar(Doador doador, LocalDate hoje) {
        List<String> motivos = new ArrayList<>();
        LocalDate dataMaisDistante = null;

        for (RegraAptidao regra : regras) {
            // POLIMORFISMO: a mesma chamada regra.avaliar(doador, hoje) executa a implementacao
            // concreta (RegraIdade, RegraPeso ou RegraIntervalo) sem que o motor saiba qual e.
            ResultadoAptidao r = regra.avaliar(doador, hoje);
            if (!r.isApto()) {
                motivos.addAll(r.getMotivos());
                LocalDate dataRegra = r.getDataLiberacao();
                if (dataRegra != null && (dataMaisDistante == null || dataRegra.isAfter(dataMaisDistante))) {
                    dataMaisDistante = dataRegra;
                }
            }
        }

        if (motivos.isEmpty()) {
            return ResultadoAptidao.apto();
        }
        return new ResultadoAptidao(false, motivos, dataMaisDistante);
    }

    /** Avalia com a data de hoje. */
    public ResultadoAptidao avaliar(Doador doador) {
        return avaliar(doador, LocalDate.now());
    }

    /**
     * Usado antes de registrar uma doacao.
     *
     * @throws DoadorInaptoException se o doador estiver inapto hoje
     */
    public void verificarOuLancar(Doador doador) throws DoadorInaptoException {
        ResultadoAptidao r = avaliar(doador);
        if (!r.isApto()) {
            throw new DoadorInaptoException(r.getMotivos(), r.getDataLiberacao());
        }
    }

    /** @return copia da lista de regras (para exibir/testar) */
    public List<RegraAptidao> getRegras() {
        return List.copyOf(regras);
    }
}
