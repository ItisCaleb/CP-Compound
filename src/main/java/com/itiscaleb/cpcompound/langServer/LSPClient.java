package com.itiscaleb.cpcompound.langServer;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.controller.BaseController;
import com.itiscaleb.cpcompound.editor.Editor;
import com.itiscaleb.cpcompound.editor.EditorContext;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageClient;

import java.util.concurrent.CompletableFuture;

public class LSPClient implements LanguageClient {

    @Override
    public void telemetryEvent(Object object) {

    }

    @Override
    public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        Editor editor = CPCompound.getEditor();
        EditorContext context = editor.getContext(diagnostics.getUri());
        if (context == null) {
            CPCompound.getLogger().debug("Diagnostic: URI {} not found", diagnostics.getUri());
            return;
        }
        context.setDiagnostics(diagnostics.getDiagnostics());
    }

    @Override
    public void showMessage(MessageParams messageParams) {
        CPCompound.getLogger().info(messageParams.getMessage());
        BaseController.getInstance().showMessageToUser(messageParams.getMessage());
    }

    @Override
    public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
        return null;
    }

    @Override
    public void logMessage(MessageParams message) {
        CPCompound.getLogger().info(message);
    }
}
