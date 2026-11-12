package com.doamais.teste;

import com.doamais.dao.ReceptorDAO;
import com.doamais.db.Database;
import com.doamais.exception.ValidacaoException;
import com.doamais.model.Doador;
import com.doamais.model.Pessoa;
import com.doamais.model.Receptor;
import com.doamais.model.Sexo;
import com.doamais.model.TipoSanguineo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Teste do modulo de Campanhas (Receptor) — responsavel: Ricardo.
 *
 * Demonstra: HERANCA, POLIMORFISMO, ENCAPSULAMENTO, tratamento de excecao,
 * colecoes (List).
 *
 * Rodar: build.bat com.doamais.teste.CampanhaTeste
 */
public class CampanhaTeste {

    public static void main(String[] args) {
        System.out.println("=== CampanhaTeste ===\n");

        // ---------------------------------------------------------------
        // Parte 1: criar campanha e registrar 5 doacoes
        // ---------------------------------------------------------------
        System.out.println("--- Parte 1: Registrar doacoes ate atingir a meta ---");
        try {
            Receptor maria = new Receptor("Maria Clara", TipoSanguineo.O_POS,
                    "Hospital Estadual Infantil", "Crianca de 7 anos internada", 5);

            for (int i = 1; i <= 5; i++) {
                maria.registrarDoacao();
                System.out.printf("  Doacao %d: progresso=%.0f%%, faltam=%d, status=%s%n",
                        i, maria.getProgresso(), maria.getFaltam(), maria.getStatus());
            }
            System.out.println("  -> Resultado final: " + maria.getProgresso() + "% / " + maria.getStatus());
            System.out.println();
        } catch (ValidacaoException e) {
            System.out.println("  ERRO inesperado na parte 1: " + e.getMessage());
        }

        // ---------------------------------------------------------------
        // Parte 2: tentar uma 6a doacao (deve lancar ValidacaoException)
        // ---------------------------------------------------------------
        System.out.println("--- Parte 2: Tentar doar apos meta atingida ---");
        try {
            Receptor maria = new Receptor("Maria Clara", TipoSanguineo.O_POS,
                    "Hospital Estadual Infantil", "Crianca de 7 anos internada", 5);
            for (int i = 0; i < 5; i++) maria.registrarDoacao();

            // 6a doacao — deve lancar excecao
            maria.registrarDoacao();
            System.out.println("  ERRO: deveria ter lancado ValidacaoException!");
        } catch (ValidacaoException e) {
            // TRATAMENTO DE EXCECAO: captura e exibe a mensagem amigavel
            System.out.println("  ValidacaoException capturada: " + e.getMessage());
        }
        System.out.println();

        // ---------------------------------------------------------------
        // Parte 3: Polimorfismo com List<Pessoa>
        // ---------------------------------------------------------------
        System.out.println("--- Parte 3: Polimorfismo (List<Pessoa>) ---");
        // COLECAO: List<Pessoa> armazena objetos de tipos diferentes
        // POLIMORFISMO: o metodo executado depende do tipo real do objeto,
        //   nao do tipo da variavel (Pessoa). Receptor.getDescricao() e
        //   Doador.getDescricao() sao implementacoes diferentes do mesmo
        //   metodo abstrato definido em Pessoa.
        try {
            List<Pessoa> pessoas = new ArrayList<>();

            // Receptor (Ricardo)
            Receptor receptor = new Receptor("Maria Clara", TipoSanguineo.O_POS,
                    "Hospital Estadual Infantil", "Crianca internada", 5);
            pessoas.add(receptor);

            // Doador (Pedro) — construtor completo
            Doador doador = new Doador(0, "Carlos Andrade", TipoSanguineo.O_NEG,
                    "12345678901", LocalDate.of(1990, 5, 15), Sexo.MASCULINO,
                    80.0, "(27) 99999-0001", "carlos@email.com", null);
            pessoas.add(doador);

            for (Pessoa p : pessoas) {
                // POLIMORFISMO: Java chama o getDescricao() da classe real
                System.out.println("  getDescricao(): " + p.getDescricao());
                // POLIMORFISMO: Java chama o validar() da classe real
                p.validar();
                System.out.println("  validar(): OK");
            }
        } catch (ValidacaoException e) {
            System.out.println("  validar() lancou: " + e.getMessage());
        } catch (UnsupportedOperationException e) {
            // Esperado se o Pedro ainda nao implementou o Doador
            System.out.println("  (Esperado) TODO do Pedro ainda nao implementado: " + e.getMessage());
        }
        System.out.println();

        // ---------------------------------------------------------------
        // Parte 4: Persistencia (depende do Gabriel — Database)
        // ---------------------------------------------------------------
        System.out.println("--- Parte 4: Persistencia (Database + ReceptorDAO) ---");
        try {
            Database.inicializar();
            ReceptorDAO dao = new ReceptorDAO();

            // 4a) Inserir uma campanha nova
            Receptor nova = new Receptor("Teste Ricardo", TipoSanguineo.A_NEG,
                    "Hospital Teste", "Campanha de teste automatico", 3);
            nova.validar();
            dao.inserir(nova);
            System.out.println("  Inserida campanha id=" + nova.getId() + ": " + nova.getDescricao());

            // 4b) Listar ativas (ordenadas por urgencia)
            List<Receptor> ativas = dao.listarAtivas();
            System.out.println("  Campanhas ativas (" + ativas.size() + "):");
            for (Receptor r : ativas) {
                System.out.printf("    id=%d %s — %.0f%% (%d/%d)%n",
                        r.getId(), r.getNome(), r.getProgresso(),
                        r.getDoacoesRecebidas(), r.getMetaDoacoes());
            }

            // 4c) Registrar uma doacao e gravar com atualizarProgresso
            nova.registrarDoacao();
            dao.atualizarProgresso(nova);
            System.out.println("  Apos 1 doacao: progresso=" + nova.getProgresso() + "%");

            // 4d) Buscar de novo por id e conferir
            Optional<Receptor> recarregada = dao.buscarPorId(nova.getId());
            if (recarregada.isPresent()) {
                Receptor r = recarregada.get();
                System.out.printf("  Recarregada do banco: id=%d, recebidas=%d, progresso=%.0f%%, status=%s%n",
                        r.getId(), r.getDoacoesRecebidas(), r.getProgresso(), r.getStatus());
            } else {
                System.out.println("  ERRO: campanha nao encontrada no banco!");
            }

            // Limpa a campanha de teste
            dao.excluir(nova.getId());
            System.out.println("  Campanha de teste removida.");

        } catch (Exception e) {
            System.out.println("  Erro na parte 4: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== FIM do CampanhaTeste ===");
    }
}
