package com.doamais.model;

import java.time.LocalDate;

/**
 * Registro de uma doacao realizada por um doador.
 *
 * ASSOCIACAO: uma Doacao TEM um Doador e (opcionalmente) um Receptor.
 * Se receptor for null, a doacao foi para o estoque geral de reposicao.
 *
 * Tabela no banco: doacoes.
 *
 * Responsavel: Amon.
 * Os metodos marcados com TODO(Amon) devem ser implementados pelo Amon.
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

    /** @return true se a doacao foi direcionada a uma campanha (receptor != null) */
    public boolean isDirecionada() {
        // TODO(Amon)
        throw new UnsupportedOperationException("TODO(Amon): Doacao.isDirecionada");
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
