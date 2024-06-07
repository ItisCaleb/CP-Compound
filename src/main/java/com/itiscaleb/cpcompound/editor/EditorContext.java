package com.itiscaleb.cpcompound.editor;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.langServer.Language;
import com.itiscaleb.cpcompound.fileSystem.FileManager;
import com.itiscaleb.cpcompound.langServer.SemanticToken;
import com.itiscaleb.cpcompound.langServer.cpp.CPPSemanticToken;
import com.itiscaleb.cpcompound.utils.SysInfo;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.SemanticTokensEdit;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;


public class EditorContext {
    private Path path;
    private String code;
    private Language lang;
    private int version;
    private boolean isTemp;
    private final BooleanProperty changed = new SimpleBooleanProperty(false);

    private final ObservableList<CompletionItem> completions = FXCollections.observableArrayList();
    private List<Integer> semantics = new ArrayList<>();

    private List<Diagnostic> diagnostics = new ArrayList<>();
    EditorContext(Path path, Language lang, String code, boolean isTemp) {
        this.path = path.normalize();
        this.code = code;
        this.lang = lang;
        this.version = 1;
        this.isTemp = isTemp;
    }

    EditorContext(Path path, String code, boolean isTemp) {
        this(path, Language.CPP, code, isTemp);
        if(!isTemp) setPath(path);
    }

    public String getCode(){
        return code;
    }

    public String getFileURI() {
        return path.toUri().toString();
    }

    public int getVersion(){
        return version;
    }

    public Language getLang(){
        return lang;
    }

    public void setCompletionList(List<CompletionItem> items){
        CPCompound.getLogger().info("Completion List");
        if(items != null){
            this.completions.setAll(items);
        }
    }

    public ObservableList<CompletionItem> getCompletionList(){
        return completions;
    }

    public void setSemantics(List<Integer> semantics){
        if(semantics != null){
            this.semantics = semantics;
        }
    }

    public List<SemanticToken> getSemanticTokens(){
        return switch (this.getLang()){
            case CPP, C-> CPPSemanticToken.fromIntList(this.semantics);
            default -> new ArrayList<>();
        };
    }

    public void editSemantics(List<SemanticTokensEdit> edits){

    }


    public void setCode(String code){
        this.code = code;
        this.version++;
        changed.set(true);
    }

    public void save(){
        if(this.changed.get()){
            FileManager.writeTextFile(this.path, code);
            this.changed.set(false);
        }
    }

    public void setPath(File file){
        this.setPath(file.toPath());
    }
    public void setPath(Path path){
        this.path = path.normalize();
        String p = this.path.toString();
        if(p.endsWith(".cpp") || p.endsWith(".cc")
                || p.endsWith(".c++")){
            this.lang = Language.CPP;
        }else if (p.endsWith(".c")){
            this.lang = Language.C;
        }else if (p.endsWith(".py")){
            this.lang = Language.Python;
        }else if (p.endsWith(".json")){
            this.lang = Language.JSON;
        }else this.lang = Language.None;
    }

    public Path getPath(){
        return path;
    }

    public void setDiagnostics(List<Diagnostic> diagnostics){
        diagnostics.sort(Comparator.comparing((Diagnostic d) -> d.getRange().getStart().getLine())
                .thenComparing(d -> d.getRange().getStart().getCharacter()));
        // segment tree here
        this.diagnostics = diagnostics;
    }

    public List<Diagnostic> getDiagnostics(){
        return diagnostics;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean temp) {
        isTemp = temp;
    }

    public Path getExePath(){
        if(this.lang == Language.Python){
            return this.path;
        }
        int dot = this.path.toString().lastIndexOf(".");
        String exe = this.path.toString().substring(0, dot);
        if (SysInfo.getOS() == SysInfo.OS.WIN){
            exe += ".exe";
        }
        return Path.of(exe);
    }

    public String getFileName(){
        return this.path.getFileName().toString();
    }

    public void addOnChanged(ChangeListener<Boolean> onChanged){
        changed.addListener(onChanged);
    }

}
