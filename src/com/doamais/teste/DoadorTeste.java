package com.doamais.teste;

import com.doamais.dao.DoadorDAO;
import com.doamais.db.Database;
import com.doamais.exception.DAOException;
import com.doamais.exception.ValidacaoException;
import com.doamais.model.Doador;
import com.doamais.model.Pessoa;
import com.doamais.model.Sexo;
import com.doamais.model.TipoSanguineo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Teste unitario e integrado do modulo de Doadores (Pedro).
 *
 * Demonstra:
 * 1. Heranca e Polimorfismo (sobrescrita de getDescricao() via List<Pessoa>);
 * 2. Validacoes de negocio e tratamento de excecoes (ValidacaoException);
 * 3. Calculo de idade com java.time.Period;
 * 4. Integracao com SQLite via DoadorDAO (insercao, busca, listagem por tipo e restricao de CPF duplicado).
 *
 * Rodar: build.bat com.doamais.teste.DoadorTeste
 */
public class DoadorTeste {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  INICIANDO TESTES DO MÓDULO DOADOR (PEDRO)       ");
        System.out.println("==================================================\n");

        testarPolimorfismo();
        testarValidacoes();
        testarCalculoIdade();
        testarPersistenciaDAO();

        System.out.println("\n==================================================");
        System.out.println("  TODOS OS TESTES CONCLUÍDOS COM SUCESSO!         ");
        System.out.println("==================================================");
    }

    /**
     * 1. Cria uma List<Pessoa> com um Doador e chama getDescricao() em cada item.
     * Demonstra o POLIMORFISMO: o metodo chamado e a versao especifica de Doador,
     * mesmo a referencia sendo da superclasse abstrata Pessoa.
     */
    private static void testarPolimorfismo() {
        System.out.println("--- 1. Teste de Polimorfismo (List<Pessoa>) ---");

        List<Pessoa> pessoas = new ArrayList<>();

        Doador doador = new Doador(
                1,
                "Ana Souza",
                TipoSanguineo.O_POS,
                "23456789012",
                LocalDate.now().minusYears(28),
                Sexo.FEMININO,
                62.0,
                "(27) 99722-3344",
                "ana.souza@email.com",
                null
        );

        pessoas.add(doador);

        for (Pessoa p : pessoas) {
            System.out.println("Pessoa: " + p.getDescricao());
        }
        System.out.println("OK: Polimorfismo testado com sucesso.\n");
    }

    /**
     * 2. Testa validar() com:
     * - um doador valido (ok);
     * - CPF com 10 digitos (invalido);
     * - peso negativo (invalido);
     * - nome vazio (invalido).
     * Para cada caso invalido, captura ValidacaoException e imprime a mensagem.
     */
    private static void testarValidacoes() {
        System.out.println("--- 2. Teste de Validações de Doador ---");

        // Caso Valido
        try {
            Doador valido = new Doador(
                    0,
                    "Lucas Martins",
                    TipoSanguineo.B_NEG,
                    "78901234567",
                    LocalDate.of(1996, 8, 25),
                    Sexo.MASCULINO,
                    70.0,
                    "(27) 99277-8899",
                    "lucas@email.com",
                    LocalDate.now().minusMonths(3)
            );
            valido.validar();
            System.out.println("[OK] Doador válido validado sem erros.");
        } catch (ValidacaoException e) {
            System.err.println("[ERRO INESPERADO] Doador válido falhou: " + e.getMessage());
        }

        // Caso Invalido: CPF com 10 digitos
        try {
            Doador cpf10 = new Doador(
                    0,
                    "Lucas Martins",
                    TipoSanguineo.B_NEG,
                    "1234567890", // apenas 10 digitos
                    LocalDate.of(1996, 8, 25),
                    Sexo.MASCULINO,
                    70.0,
                    null,
                    null,
                    null
            );
            cpf10.validar();
            System.err.println("[ERRO] Deveria ter falhado para CPF com 10 dígitos!");
        } catch (ValidacaoException e) {
            System.out.println("[OK - Exceção esperada] CPF inválido: " + e.getMessage());
        }

        // Caso Invalido: Peso negativo
        try {
            Doador pesoNeg = new Doador(
                    0,
                    "Lucas Martins",
                    TipoSanguineo.B_NEG,
                    "78901234567",
                    LocalDate.of(1996, 8, 25),
                    Sexo.MASCULINO,
                    -10.0, // peso negativo
                    null,
                    null,
                    null
            );
            pesoNeg.validar();
            System.err.println("[ERRO] Deveria ter falhado para peso negativo!");
        } catch (ValidacaoException e) {
            System.out.println("[OK - Exceção esperada] Peso inválido: " + e.getMessage());
        }

        // Caso Invalido: Nome vazio
        try {
            Doador nomeVazio = new Doador(
                    0,
                    "   ", // nome em branco
                    TipoSanguineo.B_NEG,
                    "78901234567",
                    LocalDate.of(1996, 8, 25),
                    Sexo.MASCULINO,
                    70.0,
                    null,
                    null,
                    null
            );
            nomeVazio.validar();
            System.err.println("[ERRO] Deveria ter falhado para nome vazio!");
        } catch (ValidacaoException e) {
            System.out.println("[OK - Exceção esperada] Nome vazio: " + e.getMessage());
        }

        System.out.println("OK: Validações testadas com sucesso.\n");
    }

    /**
     * 3. Testa getIdade(LocalDate.of(2026, 11, 23)) para alguem nascido em 1990-05-10.
     * Esperado: 36 anos.
     */
    private static void testarCalculoIdade() {
        System.out.println("--- 3. Teste de Cálculo de Idade ---");

        Doador d = new Doador();
        d.setDataNascimento(LocalDate.of(1990, 5, 10));

        LocalDate dataReferencia = LocalDate.of(2026, 11, 23);
        int idade = d.getIdade(dataReferencia);

        System.out.println("Data de nascimento: 1990-05-10");
        System.out.println("Data de referência: " + dataReferencia);
        System.out.println("Idade calculada: " + idade + " anos (esperado: 36)");

        if (idade != 36) {
            throw new AssertionError("Idade calculada incorreta! Esperava 36, obteve " + idade);
        }

        System.out.println("OK: Cálculo de idade correto.\n");
    }

    /**
     * 4. Teste de persistencia com SQLite (DoadorDAO):
     * - Inicializa o banco;
     * - Insere um doador;
     * - Busca por CPF;
     * - Lista por tipo;
     * - Tenta inserir o mesmo CPF de novo para verificar captura de erro de duplicidade.
     */
    private static void testarPersistenciaDAO() {
        System.out.println("--- 4. Teste de Persistência com SQLite (DoadorDAO) ---");

        try {
            Database.inicializar();
            DoadorDAO dao = new DoadorDAO();

            String cpfTeste = "99887766554";

            // Se ja existir de execucao anterior, remove para garantir repetibilidade
            Optional<Doador> anterior = dao.buscarPorCpf(cpfTeste);
            if (anterior.isPresent()) {
                dao.excluir(anterior.get().getId());
            }

            Doador novo = new Doador(
                    0,
                    "Mariana Oliveira",
                    TipoSanguineo.AB_NEG,
                    cpfTeste,
                    LocalDate.of(1995, 3, 20),
                    Sexo.FEMININO,
                    59.5,
                    "(27) 99123-4567",
                    "mariana@email.com",
                    null
            );

            novo.validar();
            dao.inserir(novo);
            System.out.println("[OK] Doador inserido com sucesso. ID gerado: " + novo.getId());

            // Buscar por CPF
            Optional<Doador> buscado = dao.buscarPorCpf(cpfTeste);
            if (buscado.isPresent()) {
                Doador d = buscado.get();
                System.out.println("[OK] Doador localizado por CPF: " + d.getNome() + " (" + d.getDescricao() + ")");
            } else {
                throw new AssertionError("Doador inserido não foi encontrado por CPF!");
            }

            // Listar por tipo
            List<Doador> listaTipo = dao.listarPorTipo(TipoSanguineo.AB_NEG);
            System.out.println("[OK] Total de doadores AB- encontrados: " + listaTipo.size());

            // Atualizar ultima doacao
            dao.atualizarUltimaDoacao(novo.getId(), LocalDate.of(2026, 10, 15));
            Optional<Doador> atualizado = dao.buscarPorId(novo.getId());
            if (atualizado.isPresent() && LocalDate.of(2026, 10, 15).equals(atualizado.get().getDataUltimaDoacao())) {
                System.out.println("[OK] Data da última doação atualizada com sucesso para "
                        + atualizado.get().getDataUltimaDoacao());
            } else {
                throw new AssertionError("Falha ao atualizar data da última doação!");
            }

            // Teste de CPF duplicado
            try {
                Doador duplicado = new Doador(
                        0,
                        "Outra Mariana",
                        TipoSanguineo.O_POS,
                        cpfTeste,
                        LocalDate.of(1992, 1, 10),
                        Sexo.FEMININO,
                        60.0,
                        null,
                        null,
                        null
                );
                dao.inserir(duplicado);
                System.err.println("[ERRO] Deveria ter rejeitado inserção de CPF duplicado!");
            } catch (DAOException e) {
                System.out.println("[OK - Exceção esperada] CPF duplicado rejeitado: " + e.getMessage());
            }

            System.out.println("OK: Persistência testada com sucesso.\n");

        } catch (Exception e) {
            System.err.println("[ERRO] Falha no teste de persistência: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

