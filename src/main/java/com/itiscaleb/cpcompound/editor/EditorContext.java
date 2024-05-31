package com.itiscaleb.cpcompound.editor;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.langServer.Language;
import com.itiscaleb.cpcompound.fileSystem.FileManager;
import com.itiscaleb.cpcompound.utils.Config;
import com.itiscaleb.cpcompound.utils.SysInfo;
import javafx.util.Pair;
import org.eclipse.lsp4j.Diagnostic;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class EditorContext {
    private Path path;
    private String code;
    private Language lang;
    private int version;
    private int lastVersion;
    private boolean isTemp;
    private boolean changed = true;
    private Path exePath;

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
        if(isTemp) this.lang = Language.CPP;
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
        return exePath;
    }


    // will return success or not
    public CompletableFuture<Pair<EditorContext, Boolean>> compile(OutputStream oStream, OutputStream errStream){
        return CompletableFuture.supplyAsync(()->{
            try {
                Config config = CPCompound.getConfig();
                String compiler = (this.lang == Language.C)   ? config.getGCCExe() :
                                  (this.lang == Language.CPP) ? config.getGPPExe() : "";
                switch (this.lang){
                    case Python -> this.exePath = this.path;
                    case CPP, C -> {
                        this.exePath = makeExePath();
                        Process p = new ProcessBuilder(compiler, path.toString(), "-o", this.exePath.toString()).start();
                        p.getInputStream().transferTo(oStream);
                        p.getErrorStream().transferTo(errStream);
                        return new Pair<>(this, p.waitFor() == 0);
                    }
                }
                return new Pair<>(this, true);
            }catch (Exception e){
                e.printStackTrace();
                return new Pair<>(this, false);
            }
        });
    }
    public CompletableFuture<Void> execute(InputStream iStream, OutputStream oStream, OutputStream errStream){
        return CompletableFuture.runAsync(()->{
            try {
                String cmd = "";
                switch (this.lang){
                    case Python -> {
                        if(SysInfo.getOS() == SysInfo.OS.WIN) cmd = "python ";
                        else cmd = "python3 ";
                    }
                    case CPP, C -> cmd = "";
                }
                cmd += this.exePath;
                Process p = new ProcessBuilder(cmd, this.exePath.toString()).start();
                p.getInputStream().transferTo(oStream);
                p.getErrorStream().transferTo(errStream);
                iStream.transferTo(p.getOutputStream());
                p.waitFor();
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }


    Path makeExePath(){
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
