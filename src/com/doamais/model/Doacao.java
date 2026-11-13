package com.doamais.model;

import java.time.LocalDate;

/**
 * Registro de uma doacao realizada por um doador.
 *
 * ASSOCIACAO: uma Doacao TEM UM Doador (obrigatorio) e PODE TER UM Receptor
 * (opcional). Os dois sao guardados como OBJETOS, e nao apenas como ids, entao
 * e possivel navegar: doacao.getDoador().getNome() ou
 * doacao.getReceptor().getNome(). Se receptor for null, a doacao foi para o
 * estoque geral de reposicao do HEMOES.
 *
 * Tabela no banco: doacoes.
 *
 * Responsavel: Amon.
 */
public class Doacao {

    private int id;
    private Doador doador;
    private Receptor receptor;   // null = estoque geral
    private LocalDate dataDoacao;
    private String local;        // ex: "HEMOES Vitória" (opcional)

    public Doacao() {
    }

    public Doacao(int id, Doador doador, Receptor receptor, LocalDate dataDoacao, String local) {
        this.id = id;
        this.doador = doador;
        this.receptor = receptor;
        this.dataDoacao = dataDoacao;
        this.local = local;
    }

    /**
     * Indica se a doacao foi direcionada a uma campanha especifica.
     *
     * @return true se a doacao foi direcionada a uma campanha (receptor != null);
     *         false se foi para o estoque geral
     */
    public boolean isDirecionada() {
        return receptor != null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Doador getDoador() {
        return doador;
    }

    public void setDoador(Doador doador) {
        this.doador = doador;
    }

    public Receptor getReceptor() {
        return receptor;
    }

    public void setReceptor(Receptor receptor) {
        this.receptor = receptor;
    }

    public LocalDate getDataDoacao() {
        return dataDoacao;
    }

    public void setDataDoacao(LocalDate dataDoacao) {
        this.dataDoacao = dataDoacao;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }
}