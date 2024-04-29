package com.itiscaleb.cpcompound.langServer;

import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageServer;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Future;

public class LSPProxy {
    String lang;
    String server;
    Process process;
    Launcher<LanguageServer> launcher;

    public LSPProxy(String lang, String server){
        this.lang = lang;
        this.server = server;
    }

    public void start() throws IOException {
        ProcessBuilder builder = new ProcessBuilder(server);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        this.process = builder.start();
        LSPClient client = new LSPClient();
        launcher = LSPLauncher.createClientLauncher(client,
                process.getInputStream(), process.getOutputStream());
        launcher.startListening();
        CompletableFuture<?> result = launcher.getRemoteProxy().getTextDocumentService().hover(
                new HoverParams(
                        new TextDocumentIdentifier("foo"),
                        new Position(0, 0)
                ));
        try {
            result.join();
        }catch (CompletionException e) {
            e.printStackTrace();
        }
    }

    public void restart() throws IOException {
        if(process != null){
            process.destroy();
            process = null;
        }
        start();
    }

    public void stop() throws IOException {}
}
