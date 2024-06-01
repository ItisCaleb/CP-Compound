package com.itiscaleb.cpcompound.editor;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.langServer.Language;
import com.itiscaleb.cpcompound.fileSystem.FileManager;
import com.itiscaleb.cpcompound.utils.Config;
import com.itiscaleb.cpcompound.utils.SysInfo;
import javafx.util.Pair;
import org.eclipse.lsp4j.Diagnostic;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class EditorContext {
    private Path path;
    private String code;
    private Language lang;
    private int version;
    private int lastVersion;
    private boolean isTemp;
    private boolean changed = true;

    private List<Diagnostic> diagnostics = new ArrayList<>();
    EditorContext(Path path, Language lang, String code, boolean isTemp) {
        this.path = path.normalize();
        this.code = code;
        this.lang = lang;
        this.version = 0;
        this.lastVersion = 0;
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

    public int getLastVersion(){
        return lastVersion;
    }

    public void setCode(String code){
        this.code = code;
        this.lastVersion = this.version;
        this.version++;
        changed = true;
    }

    public void save(){
        if(this.changed){
            FileManager.writeTextFile(this.path, code);
            this.changed = false;
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
        }else this.lang = Language.None;
    }

    public Path getPath(){
        return path;
    }

    public void setDiagnostics(List<Diagnostic> diagnostics){
        diagnostics.sort(Comparator.comparing((Diagnostic d) -> d.getRange().getStart().getLine())
                .thenComparing(d -> d.getSeverity().ordinal()));
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

}
