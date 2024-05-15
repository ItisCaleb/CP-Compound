package com.itiscaleb.cpcompound.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Config {
    public String cpp_lang_server_path = "";
    public boolean inited = false;
    transient Path path;

    public Config(Path path) {
        this.path = path;
    }

    public static Config load(String path) throws IOException {
        Path p = Paths.get(path);
        if(!Files.exists(p)) {
            Files.createFile(p);
            return new Config(p);
        }
        Config c = new Gson().fromJson(Files.readString(p), Config.class);
        c.path = p;
        return c;
    }

    public void save() {
        try{
            String json = new Gson().toJson(this);
            Files.writeString(path, json);
        }catch(IOException e){
            e.printStackTrace();
        }

    }
}
