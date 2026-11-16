package com.doamais.model;

import com.doamais.exception.ValidacaoException;

import java.time.LocalDate;
import java.time.Period;

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
        if (dataNascimento == null) {
            return 0;
        }
        LocalDate ref = (referencia != null) ? referencia : LocalDate.now();
        return Period.between(dataNascimento, ref).getYears();
    }

    /** Idade completa em anos hoje. */
    public int getIdade() {
        return getIdade(LocalDate.now());
    }

    /** Ex: "João Silva, 32 anos – O+" */
    @Override
    public String getDescricao() {
        String tipo = (tipoSanguineo != null) ? tipoSanguineo.getRotulo() : "";
        return nome + ", " + getIdade() + " anos \u2013 " + tipo;
    }

    /**
     * Regras: validarDadosBasicos(); CPF com exatamente 11 digitos;
     * data de nascimento informada e no passado; sexo informado; peso > 0;
     * data da ultima doacao (se houver) nao pode ser futura.
     */
    @Override
    public void validar() throws ValidacaoException {
        // 1. Dados basicos herdados de Pessoa (nome e tipo sanguineo obrigatorios)
        validarDadosBasicos();

        // 2. CPF: remove nao digitos, armazena no atributo e exige exatamente 11 digitos
        if (cpf == null) {
            throw new ValidacaoException("O CPF é obrigatório.");
        }
        this.cpf = cpf.replaceAll("\\D", "");
        if (this.cpf.length() != 11) {
            throw new ValidacaoException("O CPF deve conter exatamente 11 dígitos.");
        }

        // 3. Data de nascimento obrigatoria, ano valido e faixa etaria permitida (18 a 69 anos)
        if (dataNascimento == null) {
            throw new ValidacaoException("A data de nascimento é obrigatória.");
        }
        if (dataNascimento.getYear() < 1900 || !dataNascimento.isBefore(LocalDate.now())) {
            throw new ValidacaoException("Informe uma data de nascimento válida e anterior a hoje.");
        }
        int idade = getIdade();
        if (idade < 18) {
            throw new ValidacaoException("Não é permitido cadastrar doadores menores de 18 anos (idade calculada: " + idade + " anos).");
        }
        if (idade > 69) {
            throw new ValidacaoException("A idade máxima permitida para cadastro de doador é 69 anos (idade calculada: " + idade + " anos).");
        }

        // 4. Sexo obrigatorio
        if (sexo == null) {
            throw new ValidacaoException("O sexo é obrigatório.");
        }

        // 5. Peso > 0 e < 300 kg
        if (peso <= 0 || peso >= 300) {
            throw new ValidacaoException("O peso deve ser maior que 0 e menor que 300 kg.");
        }

        // 6. Data da ultima doacao, se informada, nao pode ser futura, anterior a 1900 nem anterior aos 18 anos do doador
        if (dataUltimaDoacao != null) {
            if (dataUltimaDoacao.getYear() < 1900) {
                throw new ValidacaoException("A data da última doação possui um ano inválido.");
            }
            if (dataUltimaDoacao.isAfter(LocalDate.now())) {
                throw new ValidacaoException("A data da última doação não pode ser futura.");
            }
            if (dataUltimaDoacao.isBefore(dataNascimento)) {
                throw new ValidacaoException("A data da última doação não pode ser anterior à data de nascimento.");
            }
            if (dataUltimaDoacao.isBefore(dataNascimento.plusYears(18))) {
                throw new ValidacaoException("A data da última doação é inválida (anterior aos 18 anos do doador).");
            }
        }
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
