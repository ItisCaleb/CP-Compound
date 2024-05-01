package com.itiscaleb.cpcompound.editor;

import com.itiscaleb.cpcompound.CPCompound;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Editor {
    EditorContext currentContext;
    HashMap<String, EditorContext> contexts = new HashMap<>();

    public void switchContext(String key){
        EditorContext context = contexts.get(key);
        if(context == null){
            return;
        }
        currentContext = context;
    }

    public EditorContext getCurrentContext(){
        return currentContext;
    }

    public EditorContext getContext(String key){
        return contexts.get(key);
    }

    public void addContext(String name) throws IOException {
        Path path = Path.of("tmp", name);
        String key = "file://" + path.toFile().getCanonicalPath();
        if(contexts.containsKey(key)){
            return;
        }
        EditorContext context = new EditorContext(path.toFile().getCanonicalPath(),"cpp","");
        if(currentContext == null){
            currentContext = context;
        }
        contexts.put(key, context);
        CPCompound.getLSPProxy().didOpen(context);
    }

    public void addContext(Path path) throws IOException {
        String key = "file://" + path.toFile().getCanonicalPath();
        if(contexts.containsKey(key)){
            return;
        }
        EditorContext context = new EditorContext(path.toFile().getCanonicalPath(), "cpp", Files.readString(path));
        if(currentContext == null){
            currentContext = context;
        }
        contexts.put(key, context);
        CPCompound.getLSPProxy().didOpen(context);
    }

}
