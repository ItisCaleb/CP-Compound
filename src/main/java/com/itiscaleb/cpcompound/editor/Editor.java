package com.itiscaleb.cpcompound.editor;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.langServer.LSPProxy;
import com.itiscaleb.cpcompound.langServer.Language;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.Diagnostic;
import org.fxmisc.richtext.model.StyleSpans;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Editor {
    EditorContext currentContext;
    HashMap<String, EditorContext> contexts = new HashMap<>();
    HashMap<Language, Highlighter> highlighters = new HashMap<>();

    private final ObservableList<Diagnostic> diagnostics = FXCollections.observableArrayList();
    private final ObservableList<CompletionItem> completionItems = FXCollections.observableArrayList();
    private int lastUnnamed = 0;

    public Editor() {
        highlighters.put(Language.CPP, new CppHighlighter());
        highlighters.put(Language.C, new CHighlighter());
    }

    public void switchContext(String key){
        EditorContext context = contexts.get(key);
        if(context != null){
            currentContext = context;
            refreshDiagnostic();
        }
    }

    public EditorContext getCurrentContext(){
        return currentContext;
    }

    public EditorContext getContext(String key){
        return contexts.get(key);
    }

    public String addContext() {
        return this.addContext("Untitled-"+(lastUnnamed++)+".cc");
    }

    public String addContext(String name){
        System.out.println(name);
        Path path = Path.of("tmp", name);
        String key = path.normalize().toUri().toString();
        System.out.println(key);
        if(contexts.containsKey(key)){
            return key;
        }
        EditorContext context = new EditorContext(path, Language.CPP,"",true);
        contexts.put(key, context);
        LSPProxy proxy = CPCompound.getLSPProxy(context.getLang());
        if(proxy != null){
            proxy.didOpen(context);
        }
        return key;
    }

    public String addContext(Path path) throws IOException {
        String key = path.normalize().toUri().toString();
        if(contexts.containsKey(key)){
            return key;
        }
        EditorContext context = new EditorContext(path, Files.readString(path), false);
        contexts.put(key, context);
        LSPProxy proxy = CPCompound.getLSPProxy(context.getLang());
        if(proxy != null){
            proxy.didOpen(context);
        }
        return key;
    }

    public void removeContext(String name) {
        EditorContext context = contexts.remove(name);
        if(context != null){
            LSPProxy proxy = CPCompound.getLSPProxy(context.getLang());
            if(proxy != null){
                proxy.didClose(context);
            }
        }
    }

    public void refreshDiagnostic(){
        this.diagnostics.setAll(currentContext.getDiagnostics());
    }

    public void setCompletionList(List<CompletionItem> items){
        System.out.println("Completion List");
        if(items != null){
            this.completionItems.setAll(items);
        }
    }

    public HashMap<String, EditorContext> getContexts(){
        return contexts;
    }

    public ObservableList<CompletionItem> getCompletionList(){
        return this.completionItems;
    }

    public ObservableList<Diagnostic> getDiagnostics() {
        return this.diagnostics;
    }

    public StyleSpans<Collection<String>> computeHighlighting(String key) {
        EditorContext context = contexts.get(key);
        return computeHighlighting(context);
    }

    public StyleSpans<Collection<String>> computeHighlighting(EditorContext context) {
        return highlighters.get(context.getLang()).computeHighlighting(context.getCode());
    }
}
