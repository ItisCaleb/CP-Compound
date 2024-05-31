package com.itiscaleb.cpcompound.langServer;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.editor.EditorContext;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageServer;

import java.io.IOException;
import java.util.ArrayList;

public class LSPProxy {
    Process process;
    Launcher<LanguageServer> launcher = null;
    String[] args;

    public LSPProxy(String remotePath, String... args){
        this.args = new String[args.length + 1];
        this.args[0] = remotePath;
        System.arraycopy(args, 0, this.args, 1, args.length);
    }

    public void start() {
        try{
            ProcessBuilder builder = new ProcessBuilder(args);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            this.process = builder.start();
            LSPClient client = new LSPClient();
            this.launcher = LSPLauncher.createClientLauncher(client,
                    process.getInputStream(), process.getOutputStream());
            launcher.startListening();
            init();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    private void init(){
        LanguageServer s = launcher.getRemoteProxy();
        var future = s.initialize(new InitializeParams());
        future.join();
        s.initialized(new InitializedParams());
    }

    public void restart() throws IOException {
        stop();
        start();
    }

    public void didOpen(EditorContext context){
        if(launcher == null){
            return;
        }
        DidOpenTextDocumentParams params = new DidOpenTextDocumentParams();
        params.setTextDocument(new TextDocumentItem(
                context.getFileURI(),
                context.getLang().lang,
                context.getVersion(), ""));
        launcher.getRemoteProxy()
                .getTextDocumentService()
                .didOpen(params);
    }

    public void didChange(EditorContext context){
        if(launcher == null){
            return;
        }
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

    public void requestCompletion(EditorContext context, Position position){
        if(launcher == null){
            return;
        }
        CompletionParams params = new CompletionParams();
        params.setTextDocument(new VersionedTextDocumentIdentifier(
                context.getFileURI(),
                context.getLastVersion()));
        params.setPosition(position);
        var future = launcher.getRemoteProxy()
                .getTextDocumentService()
                .completion(params);
        future.whenComplete((result, throwable) -> {
            CPCompound.getEditor().setCompletionList(result.getRight().getItems());
        });
    }

    public void didClose(EditorContext context){
        if(launcher == null){
            return;
        }
        DidCloseTextDocumentParams params = new DidCloseTextDocumentParams();
        params.setTextDocument(new VersionedTextDocumentIdentifier(
                context.getFileURI(),
                context.getLastVersion()));
        launcher.getRemoteProxy()
                .getTextDocumentService()
                .didClose(params);
    }

    public void stop() {
        if(launcher == null){
            return;
        }
        if(process != null){
            launcher.getRemoteProxy().exit();
            process.destroy();
            process = null;
        }
    }
}
