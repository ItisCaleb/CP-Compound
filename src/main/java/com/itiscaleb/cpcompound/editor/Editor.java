package com.itiscaleb.cpcompound.editor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.highlighter.*;
import com.itiscaleb.cpcompound.langServer.cpp.CompileCommand;
import com.itiscaleb.cpcompound.langServer.LSPProxy;
import com.itiscaleb.cpcompound.langServer.Language;
import com.itiscaleb.cpcompound.utils.APPData;
import com.itiscaleb.cpcompound.utils.Config;
import com.itiscaleb.cpcompound.utils.SysInfo;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.util.Pair;
import org.fxmisc.richtext.model.StyleSpans;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Editor {
    ObjectProperty<EditorContext> currentContext = new SimpleObjectProperty<>();
    HashMap<String, EditorContext> contexts = new HashMap<>();
    HashMap<Language, Highlighter> highlighters = new HashMap<>();

    private int lastUnnamed = 0;

    private Process currentProcess = null;

    public Editor() {
        highlighters.put(Language.CPP, new CppHighlighter());
        highlighters.put(Language.C, new CHighlighter());
        highlighters.put(Language.Python, new PythonHighlighter());
        highlighters.put(Language.JSON, new JSONHighlighter());
        highlighters.put(Language.None, new NoneHighlighter());
    }

    public void switchContext(String key){
        EditorContext context = contexts.get(key);
        if(context != null){
            currentContext.setValue(context);
        }
    }

    public EditorContext getCurrentContext(){
        return currentContext.get();
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
        if(contexts.isEmpty()){
            currentContext.setValue(null);
        }
        generateCompileCommands();
        if(context != null){
            LSPProxy proxy = CPCompound.getLSPProxy(context.getLang());
            if(proxy != null){
                proxy.didClose(context);
            }
        }
    }

    // will return success or not
    public CompletableFuture<Pair<EditorContext, Boolean>> compile(EditorContext context,
                                                                   OutputStream oStream, OutputStream errStream){
        return CompletableFuture.supplyAsync(()->{
            try {
                Language lang = context.getLang();
                Config config = CPCompound.getConfig();
                String compiler = (lang == Language.C)   ? config.getGCCExe() :
                        (lang == Language.CPP) ? config.getGPPExe() : "";
                switch (context.getLang()){
                    case CPP, C -> {

                        Process p = new ProcessBuilder(compiler,
                                context.getPath().toString(),
                                "-o", context.getExePath().toString()).start();
                        p.getInputStream().transferTo(oStream);
                        p.getErrorStream().transferTo(errStream);
                        return new Pair<>(context, p.waitFor() == 0);
                    }
                    case None -> {
                        return new Pair<>(context, false);
                    }
                }
                return new Pair<>(context, true);
            }catch (Exception e){
                CPCompound.getLogger().error("Error occurred", e);
                return new Pair<>(context, false);
            }
        });
    }
    public CompletableFuture<Void> execute(EditorContext context,
                                           InputStream iStream, OutputStream oStream,
                                           OutputStream errStream, boolean writeOnce){
        return CompletableFuture.runAsync(()->{
            try {
                if(currentProcess != null) return;
                Language lang = context.getLang();
                String exe = context.getExePath().toString();
                Process p;
                switch (lang){
                    case Python -> {
                        if(SysInfo.getOS() == SysInfo.OS.WIN){
                            p = new ProcessBuilder("python", exe).start();
                        }
                        else p = new ProcessBuilder("python3", exe).start();
                    }
                    case CPP, C -> p = new ProcessBuilder(exe).start();
                    default -> {
                        return;
                    }
                }
                currentProcess = p;
                do{
                    iStream.transferTo(p.getOutputStream());
                    p.getOutputStream().flush();
                }while (p.isAlive() && !writeOnce);
                p.getOutputStream().close();
                p.getInputStream().transferTo(oStream);
                p.getErrorStream().transferTo(errStream);
                p.waitFor();
                currentProcess = null;
            }catch (Exception e){
                CPCompound.getLogger().error("Error occurred", e);
            }
        });
    }

    public void stopExecute(){
        if(currentProcess != null){
            currentProcess.destroyForcibly();
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
            CPCompound.getLogger().error("Error occurred", e);
        }
    }

    public HashMap<String, EditorContext> getContexts(){
        return contexts;
    }

    public StyleSpans<Collection<String>> computeHighlighting(String key) {
        EditorContext context = contexts.get(key);
        return computeHighlighting(context);
    }

    public StyleSpans<Collection<String>> computeHighlighting(EditorContext context) {
        return highlighters.get(context.getLang()).computeHighlighting(context.getCode());
    }

    public void addOnSwitch(ChangeListener<EditorContext> listener){
        currentContext.addListener(listener);
    }
}
