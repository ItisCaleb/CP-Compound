package com.itiscaleb.cpcompound.editor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.eclipse.lsp4j.Diagnostic;

import java.util.List;

public class EditorContext {
    private String filePath;
    private String code;
    private String lang;
    private int version;
    private int lastVersion;
    private final ObservableList<Diagnostic> diagnostics = FXCollections.observableArrayList();
    EditorContext(String filePath, String lang, String code) {
        this.filePath = filePath;
        this.code = code;
        this.lang = lang;
        this.version = 0;
        this.lastVersion = 0;
    }

    public String getCode(){
        return code;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileURI() {
        return "file://" + filePath;
    }

    public int getVersion(){
        return version;
    }

    public String getLang(){
        return lang;
    }

    public int getLastVersion(){
        return lastVersion;
    }

    public void setCode(String code){
        this.code = code;
        this.lastVersion = this.version;
        this.version++;
    }

    public void setDiagnostics(List<Diagnostic> diagnostics){
        this.diagnostics.setAll(diagnostics);
    }

    public ObservableList<Diagnostic> getDiagnostics(){
        return diagnostics;
    }


}
