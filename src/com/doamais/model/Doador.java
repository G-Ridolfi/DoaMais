package com.doamais.model;

import com.doamais.exception.ValidacaoException;

import java.time.LocalDate;

/**
 * Voluntario doador de sangue. HERANCA: estende Pessoa.
 *
 * Tabela no banco: doadores.
 *
 * Responsavel: Pedro.
 * CONTRATO: atributos, construtores e assinaturas abaixo sao usados por
 * Guilherme (aptidao) e Amon (doacoes). Nao alterar sem avisar o grupo.
 * Os metodos marcados com TODO(Pedro) devem ser implementados pelo Pedro.
 */
public class Doador extends Pessoa {

    private String cpf;               // apenas 11 digitos, sem pontos
    private LocalDate dataNascimento;
    private Sexo sexo;
    private double peso;              // em kg
    private String telefone;          // opcional
    private String email;             // opcional
    private LocalDate dataUltimaDoacao; // null = nunca doou

    /** Construtor vazio (usado ao montar o objeto a partir de formulario). */
    public Doador() {
    }

    /** Construtor completo (usado pelo DoadorDAO.mapear). */
    public Doador(int id, String nome, TipoSanguineo tipoSanguineo, String cpf,
                  LocalDate dataNascimento, Sexo sexo, double peso,
                  String telefone, String email, LocalDate dataUltimaDoacao) {
        super(id, nome, tipoSanguineo);
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.peso = peso;
        this.telefone = telefone;
        this.email = email;
        this.dataUltimaDoacao = dataUltimaDoacao;
    }

    /**
     * Idade completa em anos na data de referencia.
     * Usado pela RegraIdade do Guilherme.
     */
    public int getIdade(LocalDate referencia) {
        // TODO(Pedro): calcular com Period.between(dataNascimento, referencia).getYears()
        throw new UnsupportedOperationException("TODO(Pedro): Doador.getIdade");
    }

    /** Idade completa em anos hoje. */
    public int getIdade() {
        return getIdade(LocalDate.now());
    }

    /** Ex: "João Silva, 32 anos – O+" */
    @Override
    public String getDescricao() {
        // TODO(Pedro): montar descricao com nome, idade e tipo sanguineo
        throw new UnsupportedOperationException("TODO(Pedro): Doador.getDescricao");
    }

    /**
     * Regras: validarDadosBasicos(); CPF com exatamente 11 digitos;
     * data de nascimento informada e no passado; sexo informado; peso > 0;
     * data da ultima doacao (se houver) nao pode ser futura.
     */
    @Override
    public void validar() throws ValidacaoException {
        // TODO(Pedro): implementar as regras descritas no Javadoc
        throw new UnsupportedOperationException("TODO(Pedro): Doador.validar");
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataUltimaDoacao() {
        return dataUltimaDoacao;
    }

    public void setDataUltimaDoacao(LocalDate dataUltimaDoacao) {
        this.dataUltimaDoacao = dataUltimaDoacao;
    }
}
