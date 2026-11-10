package com.doamais;

import com.doamais.db.Database;
import com.doamais.handler.AptidaoHandler;
import com.doamais.handler.DoacaoHandler;
import com.doamais.handler.DoadorHandler;
import com.doamais.handler.ExportarHandler;
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
 *   /exportar   ExportarHandler  (Gabriel)
 *   /static/    arquivos de web/ (Maria Vitoria)
 * </pre>
 *
 * Responsavel: Maria Vitoria. Os outros integrantes NAO editam este arquivo.
 */
public class Main {

    public static final int PORTA = 8080;

    public static void main(String[] args) throws IOException {
        Database.inicializar();

        HttpServer servidor = HttpServer.create(new InetSocketAddress(PORTA), 0);
        servidor.createContext("/", new HomeHandler());
        servidor.createContext("/doadores", new DoadorHandler());
        servidor.createContext("/aptidao", new AptidaoHandler());
        servidor.createContext("/campanhas", new ReceptorHandler());
        servidor.createContext("/doacoes", new DoacaoHandler());
        servidor.createContext("/exportar", new ExportarHandler());
        // TODO(Maria): servidor.createContext("/static/", ...) servindo os arquivos da pasta web/
        //   (style.css, app.js) com o Content-Type correto; bloquear ".." no caminho.

        // Varias threads: o servidor atende requisicoes em paralelo
        servidor.setExecutor(Executors.newFixedThreadPool(8));
        servidor.start();
        System.out.println("DoaMais rodando em http://localhost:" + PORTA);
    }
}
