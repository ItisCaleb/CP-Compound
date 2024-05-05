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
        if(context != null){
            currentContext = context;
        }
    }

    public EditorContext getCurrentContext(){
        return currentContext;
    }

    public EditorContext getContext(String key){
        return contexts.get(key);
    }

    public String addContext(String name) throws IOException {
        Path path = Path.of("tmp", name);
        String key = "file://" + path.toFile().getCanonicalPath();
        if(contexts.containsKey(key)){
            return key;
        }
        EditorContext context = new EditorContext(path.toFile().getCanonicalPath(),"cpp","");
        contexts.put(key, context);
        CPCompound.getLSPProxy().didOpen(context);
        return key;
    }

    public String addContext(Path path) throws IOException {
        String key = "file://" + path.toFile().getCanonicalPath();
        if(contexts.containsKey(key)){
            return key;
        }
        EditorContext context = new EditorContext(path.toFile().getCanonicalPath(), "cpp", Files.readString(path));
        contexts.put(key, context);
        CPCompound.getLSPProxy().didOpen(context);
        return key;
    }

}
