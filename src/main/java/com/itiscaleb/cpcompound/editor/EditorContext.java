package com.itiscaleb.cpcompound.editor;

import com.itiscaleb.cpcompound.langServer.Language;
import com.itiscaleb.cpcompound.fileSystem.FileManager;
import org.eclipse.lsp4j.Diagnostic;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class EditorContext {
    private Path path;
    private String code;
    private Language lang;
    private int version;
    private int lastVersion;
    private boolean hasChanged;
    private boolean isTemp;
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
        setPath(path);
        this.code = code;
        this.version = 0;
        this.lastVersion = 0;
        this.isTemp = isTemp;
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
        this.hasChanged = true;
    }

    public void save(){
        this.hasChanged = false;
        FileManager.writeTextFile(this.path, code);
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
}
