package com.doamais;

import com.doamais.db.Database;
import com.doamais.handler.AptidaoHandler;
import com.doamais.handler.ArquivoEstaticoHandler;
import com.doamais.handler.DoacaoHandler;
import com.doamais.handler.DoadorHandler;
import com.doamais.handler.HomeHandler;
import com.doamais.handler.ReceptorHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Ponto de entrada do DoaMais: inicializa o banco e sobe o servidor HTTP
 * nativo do JDK (com.sun.net.httpserver) na porta 8080.
 *
 * Rotas (CONTRATO, ver docs/CONTRATOS.md):
 * <pre>
 *   /           HomeHandler      (Maria Vitoria)
 *   /doadores   DoadorHandler    (Pedro)
 *   /aptidao    AptidaoHandler   (Guilherme)
 *   /campanhas  ReceptorHandler  (Ricardo)
 *   /doacoes    DoacaoHandler    (Amon)
 *   /static/    arquivos de web/ (Maria Vitoria)
 * </pre>
 *
 * Responsavel: Maria Vitoria. Os outros integrantes NAO editam este arquivo.
 */
public class Main {

    public static final int PORTA = 8080;

    public static void main(String[] args) throws IOException {
        // #1 Inicializa o banco SQLite e os dados iniciais antes de aceitar acessos
        Database.inicializar();

        HttpServer servidor = HttpServer.create(new InetSocketAddress(PORTA), 0);
        // #2 Registra as rotas de cada integrante (mantem o projeto modular).
        // Polimorfismo de interface: createContext aceita classes diferentes pelo contrato HttpHandler.
        servidor.createContext("/", new HomeHandler());
        servidor.createContext("/doadores", new DoadorHandler());
        servidor.createContext("/aptidao", new AptidaoHandler());
        servidor.createContext("/campanhas", new ReceptorHandler());
        servidor.createContext("/doacoes", new DoacaoHandler());
        // Arquivos estaticos (style.css, app.js) da pasta web/; ".." e bloqueado no handler
        servidor.createContext("/static/", new ArquivoEstaticoHandler("web"));

        // #3 Pool de 8 threads: atende varios usuarios ao mesmo tempo na porta 8080
        servidor.setExecutor(Executors.newFixedThreadPool(8));
        servidor.start();
        System.out.println("DoaMais rodando em http://localhost:" + PORTA);
    }
}
