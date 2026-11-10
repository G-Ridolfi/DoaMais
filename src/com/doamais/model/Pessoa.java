package com.doamais.model;

import com.doamais.exception.ValidacaoException;

/**
 * Superclasse ABSTRATA de todas as pessoas do sistema.
 *
 * HERANCA: Doador e Receptor estendem Pessoa e reaproveitam id, nome e tipo
 * sanguineo. POLIMORFISMO: cada subclasse sobrescreve getDescricao() e
 * validar() do seu jeito; quem tem uma referencia do tipo Pessoa chama o
 * metodo certo sem saber qual subclasse e.
 * <pre>
 *   List&lt;Pessoa&gt; pessoas = List.of(doador, receptor);
 *   for (Pessoa p : pessoas) System.out.println(p.getDescricao());
 * </pre>
 * Pessoa nao pode ser instanciada (new Pessoa() nao compila).
 *
 * Responsavel: Pedro. (CONTRATO: nao alterar assinaturas sem avisar o grupo.)
 */
public abstract class Pessoa {

    protected int id;
    protected String nome;
    protected TipoSanguineo tipoSanguineo;

    protected Pessoa() {
    }

    protected Pessoa(int id, String nome, TipoSanguineo tipoSanguineo) {
        this.id = id;
        this.nome = nome;
        this.tipoSanguineo = tipoSanguineo;
    }

    /** @return texto curto para exibir em listas e cards */
    public abstract String getDescricao();

    /**
     * Confere se os dados do objeto sao validos.
     *
     * @throws ValidacaoException com mensagem amigavel se algo estiver errado
     */
    public abstract void validar() throws ValidacaoException;

    /** Validacao comum a todas as pessoas; as subclasses chamam no seu validar(). */
    protected void validarDadosBasicos() throws ValidacaoException {
        if (nome == null || nome.isBlank()) {
            throw new ValidacaoException("O nome é obrigatório.");
        }
        if (tipoSanguineo == null) {
            throw new ValidacaoException("O tipo sanguíneo é obrigatório.");
        }
    }

    /** id gerado pelo banco; 0 enquanto o objeto ainda nao foi salvo */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoSanguineo getTipoSanguineo() {
        return tipoSanguineo;
    }

    public void setTipoSanguineo(TipoSanguineo tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }

    /** Polimorfismo: imprime a descricao especifica de cada subclasse. */
    @Override
    public String toString() {
        return getDescricao();
    }
}
