package com.itiscaleb.cpcompound.editor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.langServer.CompileCommand;
import com.itiscaleb.cpcompound.langServer.LSPProxy;
import com.itiscaleb.cpcompound.langServer.Language;
import com.itiscaleb.cpcompound.utils.APPData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.Diagnostic;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
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
        highlighters.put(Language.Python, new PythonHighlighter());
        highlighters.put(Language.None, new NoneHighlighter());
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

    public String addContext() throws IOException {
        return this.addContext("Untitled-"+(lastUnnamed++)+".cc");
    }

    public String addContext(String name) throws IOException {
        Path path = APPData.resolve("tmp").resolve(name);
        return addContext(path, true);
    }

    public String addContext(Path path, boolean isTmp) throws IOException {
        String key = path.normalize().toUri().toString();
        if(contexts.containsKey(key)){
            return key;
        }
        EditorContext context = new EditorContext(path, isTmp?"":Files.readString(path), isTmp);
        contexts.put(key, context);
        generateCompileCommands();

        // open to lsp
        LSPProxy proxy = CPCompound.getLSPProxy(context.getLang());
        if(proxy != null){
            proxy.didOpen(context);
        }
        return key;
    }

    public void removeContext(String name) {
        EditorContext context = contexts.remove(name);
        generateCompileCommands();
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

    private void generateCompileCommands(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<CompileCommand> commands = new ArrayList<>();
        for(EditorContext context : this.contexts.values()){
            commands.add(CompileCommand.fromContext(context));
        }
        String s = gson.toJson(commands);
        try {
            Files.writeString(APPData.resolve("compile_commands.json")
                    , s, StandardCharsets.UTF_8);

        }catch (Exception e){
            e.printStackTrace();
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
