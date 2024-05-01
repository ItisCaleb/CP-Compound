package com.itiscaleb.cpcompound.langServer;

import com.itiscaleb.cpcompound.editor.EditorContext;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class LSPProxy {
    String lang;
    String remotePath;
    Process process;
    Launcher<LanguageServer> launcher;

    public LSPProxy(String lang, String remotePath){
        this.lang = lang;
        this.remotePath = remotePath;
    }

    public void start() throws IOException {
        ProcessBuilder builder = new ProcessBuilder(remotePath);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        this.process = builder.start();
        LSPClient client = new LSPClient();
        this.launcher = LSPLauncher.createClientLauncher(client,
                process.getInputStream(), process.getOutputStream());
        launcher.startListening();
        init();
    }
    private void init(){
        LanguageServer s = launcher.getRemoteProxy();
        CompletableFuture<?> future = s.initialize(new InitializeParams());
        future.join();
        s.initialized(new InitializedParams());
    }

    public void restart() throws IOException {
        if(process != null){
            launcher.getRemoteProxy().exit();
            process.destroy();
            process = null;
        }
        start();
    }

    public void didOpen(EditorContext context){
        DidOpenTextDocumentParams params = new DidOpenTextDocumentParams();
        params.setTextDocument(new TextDocumentItem(
                context.getFileURI(),
                context.getLang(),
                context.getVersion(), ""));
        launcher.getRemoteProxy()
                .getTextDocumentService()
                .didOpen(params);
    }

    public void didChange(EditorContext context){
        DidChangeTextDocumentParams params = new DidChangeTextDocumentParams();
        params.setTextDocument(
                new VersionedTextDocumentIdentifier(
                        context.getFileURI(),
                        context.getLastVersion()));
        ArrayList<TextDocumentContentChangeEvent> list = new ArrayList<>();
        list.add(new TextDocumentContentChangeEvent(context.getCode()));
        params.setContentChanges(list);
        launcher.getRemoteProxy()
                .getTextDocumentService()
                .didChange(params);
    }

    public void stop() throws IOException {}
}
