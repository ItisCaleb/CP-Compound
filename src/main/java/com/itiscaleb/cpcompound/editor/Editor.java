package com.itiscaleb.cpcompound.editor;

import java.util.HashMap;

public class Editor {
    static Editor instance;
    EditorContext currentContext;
    HashMap<String, EditorContext> contexts = new HashMap<>();
    public static Editor getInstance(){
        if(instance == null){
            instance = new Editor();
        }
        return instance;
    }

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

    public void addContext(String key){
        if(contexts.containsKey(key)){
            return;
        }
        EditorContext context = new EditorContext();
        if(currentContext == null){
            currentContext = context;
        }
        contexts.put(key, context);
    }

}
