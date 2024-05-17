package com.itiscaleb.cpcompound.editor;

import com.itiscaleb.cpcompound.langServer.Language;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.eclipse.lsp4j.Diagnostic;

import java.util.ArrayList;
import java.util.List;

public class EditorContext {
    private String fileURI;
    private String code;
    private Language lang;
    private int version;
    private int lastVersion;
    private boolean hasChanged;
    private List<Diagnostic> diagnostics = new ArrayList<>();
    EditorContext(String fileURI, Language lang, String code) {
        this.fileURI = fileURI;
        this.code = code;
        this.lang = lang;
        this.version = 0;
        this.lastVersion = 0;
    }

    EditorContext(String fileURI, String code) {
        this.fileURI = fileURI;
        this.code = code;
        this.version = 0;
        this.lastVersion = 0;
        if(this.fileURI.endsWith(".cpp") || this.fileURI.endsWith(".cc")
                || this.fileURI.endsWith(".c++")){
            this.lang = Language.CPP;
        }else if (this.fileURI.endsWith(".c")){
            this.lang = Language.C;
        }else if (this.fileURI.endsWith(".py")){
            this.lang = Language.Python;
        }else
            this.lang = Language.None;

    }

    public String getCode(){
        return code;
    }

    public String getFileURI() {
        return fileURI;
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

    void save(){
        this.hasChanged = false;
    }

    public void setDiagnostics(List<Diagnostic> diagnostics){
        this.diagnostics = diagnostics;
    }

    public List<Diagnostic> getDiagnostics(){
        return diagnostics;
    }


}
