package com.doamais.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Serve os arquivos estaticos da pasta web/ (CSS, JS e imagens) na rota
 * "/static/". Ex: /static/style.css -> web/style.css.
 *
 * Implementa HttpHandler diretamente (e nao herda BaseHandler) porque envia
 * bytes de arquivos, e nao paginas HTML geradas.
 *
 * LEITURA DE ARQUIVO: Files.readAllBytes le o arquivo do disco.
 * SEGURANCA: caminhos com ".." sao recusados (evita ler arquivos fora de web/).
 *
 * Responsavel: Maria Vitoria.
 */
// #24 Implementa HttpHandler direto (nao BaseHandler) por entregar apenas arquivos prontos
public class ArquivoEstaticoHandler implements HttpHandler {

    /** Prefixo da rota registrada no Main. */
    private static final String PREFIXO = "/static/";

    // #25 Colecao (HashMap): extensao -> Content-Type (busca rapida sem depender de ordem)
    private static final Map<String, String> TIPOS = new HashMap<>();

    static {
        TIPOS.put("css", "text/css; charset=UTF-8");
        TIPOS.put("js", "application/javascript; charset=UTF-8");
        TIPOS.put("svg", "image/svg+xml");
        TIPOS.put("png", "image/png");
        TIPOS.put("jpg", "image/jpeg");
        TIPOS.put("ico", "image/x-icon");
    }

    private final Path pastaWeb;

    /** @param pastaWeb pasta de onde os arquivos sao lidos (ex: "web") */
    public ArquivoEstaticoHandler(String pastaWeb) {
        this.pastaWeb = Paths.get(pastaWeb).toAbsolutePath().normalize();
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        try {
            String caminho = ex.getRequestURI().getPath();
            // #26 Seguranca: bloqueia ".." e confirma startsWith para nao ler arquivos fora de web/
            if (!"GET".equalsIgnoreCase(ex.getRequestMethod())
                    || !caminho.startsWith(PREFIXO) || caminho.contains("..")) {
                enviarNaoEncontrado(ex);
                return;
            }

            Path arquivo = pastaWeb.resolve(caminho.substring(PREFIXO.length())).normalize();
            // Segunda barreira: o arquivo resolvido precisa continuar dentro de web/
            if (!arquivo.startsWith(pastaWeb) || !Files.isRegularFile(arquivo)) {
                enviarNaoEncontrado(ex);
                return;
            }

            // #27 Leitura de arquivo: carrega CSS e JS direto da pasta web/ no disco
            byte[] corpo = Files.readAllBytes(arquivo);
            ex.getResponseHeaders().set("Content-Type", tipoDoArquivo(arquivo));
            ex.sendResponseHeaders(200, corpo.length);
            try (OutputStream saida = ex.getResponseBody()) {
                saida.write(corpo);
            }
        } finally {
            ex.close();
        }
    }

    /** @return Content-Type de acordo com a extensao do arquivo */
    private String tipoDoArquivo(Path arquivo) {
        String nome = arquivo.getFileName().toString().toLowerCase();
        int ponto = nome.lastIndexOf('.');
        String extensao = ponto >= 0 ? nome.substring(ponto + 1) : "";
        return TIPOS.getOrDefault(extensao, "application/octet-stream");
    }

    /** Resposta 404 em texto simples. */
    private void enviarNaoEncontrado(HttpExchange ex) throws IOException {
        byte[] corpo = "404 - Arquivo não encontrado".getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        ex.sendResponseHeaders(404, corpo.length);
        try (OutputStream saida = ex.getResponseBody()) {
            saida.write(corpo);
        }
    }
}
