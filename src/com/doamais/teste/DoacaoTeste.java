package com.doamais.teste;

import com.doamais.dao.DoacaoDAO;
import com.doamais.dao.EstoqueDAO;
import com.doamais.db.Database;
import com.doamais.exception.DoadorInaptoException;
import com.doamais.model.Doacao;
import com.doamais.model.Doador;
import com.doamais.model.NivelEstoque;
import com.doamais.model.Receptor;
import com.doamais.model.Sexo;
import com.doamais.model.TipoSanguineo;
import com.doamais.dao.ReceptorDAO;
import com.doamais.service.DoacaoService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Teste manual do modulo de Doacoes (Amon).
 * Rodar: build.bat com.doamais.teste.DoacaoTeste
 *
 * Parte 1: colecoes em memoria (nao usa banco).
 * Parte 2: termometro e contagem por tipo (depende do banco).
 * Parte 3: cenario da demo com o DoacaoService (altera o banco!).
 *
 * Para repetir a parte 3 do zero, apague o arquivo database/doamais.db.
 */
public class DoacaoTeste {

    public static void main(String[] args) throws Exception {
        parte1ColecoesEmMemoria();
        Database.inicializar();
        parte2TermometroEContagem();
        parte3CenarioDaDemo();
    }

    private static void parte1ColecoesEmMemoria() throws Exception {
        System.out.println("=== Parte 1: colecoes em memoria ===");
        Doador ana = new Doador(1, "Ana Souza", TipoSanguineo.O_POS, "23456789012",
                LocalDate.of(1998, 6, 10), Sexo.deCodigo("F"), 62.0, null, null, null);
        Doador lucas = new Doador(2, "Lucas Martins", TipoSanguineo.B_NEG, "78901234567",
                LocalDate.of(1996, 8, 25), Sexo.deCodigo("M"), 70.0, null, null, null);
        Receptor campanha = new Receptor("Maria Clara", TipoSanguineo.O_POS,
                "Hospital Estadual Infantil", "Historia de teste", 5);

        // COLECAO (List): guarda as doacoes na ordem de insercao
        List<Doacao> doacoes = new ArrayList<>();
        doacoes.add(new Doacao(1, ana, campanha, LocalDate.now(), "HEMOES - Vitória"));
        doacoes.add(new Doacao(2, lucas, null, LocalDate.now(), "HEMOES - Vitória"));
        doacoes.add(new Doacao(3, ana, null, LocalDate.now(), "HEMOES - Vitória"));

        // COLECAO (Map + EnumMap): conta por tipo com merge()
        Map<TipoSanguineo, Integer> porTipo = new EnumMap<>(TipoSanguineo.class);
        for (Doacao d : doacoes) {
            porTipo.merge(d.getDoador().getTipoSanguineo(), 1, Integer::sum);
            System.out.println("Doacao " + d.getId() + " de " + d.getDoador().getNome()
                    + " -> direcionada? " + d.isDirecionada());
        }
        System.out.println("Contagem por tipo: " + porTipo);
    }

    private static void parte2TermometroEContagem() throws Exception {
        System.out.println("\n=== Parte 2: termometro e contagem (banco) ===");
        EnumMap<TipoSanguineo, NivelEstoque> termometro = new EstoqueDAO().carregarTermometro();
        for (Map.Entry<TipoSanguineo, NivelEstoque> e : termometro.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue().getDescricao());
        }
        System.out.println("Doacoes por tipo: " + new DoacaoDAO().contarPorTipo());
    }

    private static void parte3CenarioDaDemo() throws Exception {
        System.out.println("\n=== Parte 3: cenario da demo ===");
        DoacaoService service = new DoacaoService();
        ReceptorDAO receptorDAO = new ReceptorDAO();
        final int CARLOS = 1;       // doou em 2026-10-29
        final int ANA = 2;          // nunca doou
        final int MARIA_CLARA = 1;  // campanha O+, 3 de 5

        // Doador INAPTO: a excecao sobe do motor ate aqui
        try {
            service.registrar(CARLOS, MARIA_CLARA, LocalDate.now(), "HEMOES - Vitória");
            System.out.println("ERRO: Carlos deveria estar inapto!");
        } catch (DoadorInaptoException e) {
            System.out.println("Carlos bloqueado. Motivos: " + e.getMotivos());
            System.out.println("Liberado a partir de: " + e.getDataLiberacao());
        }

        // Doador APTO: progresso antes e depois
        Receptor antes = receptorDAO.buscarPorId(MARIA_CLARA).orElseThrow();
        System.out.println("Antes : " + antes.getDoacoesRecebidas() + "/" + antes.getMetaDoacoes()
                + " (" + Math.round(antes.getProgresso()) + "%)");

        Doacao d = service.registrar(ANA, MARIA_CLARA, LocalDate.now(), "HEMOES - Vitória");
        System.out.println("Doacao registrada com id " + d.getId());

        Receptor depois = receptorDAO.buscarPorId(MARIA_CLARA).orElseThrow();
        System.out.println("Depois: " + depois.getDoacoesRecebidas() + "/" + depois.getMetaDoacoes()
                + " (" + Math.round(depois.getProgresso()) + "%)");
    }
}