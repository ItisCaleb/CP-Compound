package com.itiscaleb.cpcompound.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Config {
    public String cpp_lang_server_path = "";
    public boolean lang_server_downloaded = false;
    public String gcc_path = "";
    public boolean gcc_downloaded = false;
    transient Path path;
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Config(Path path) {
        this.path = path;
    }

    public static Config load(Path path) throws IOException {
        if(!Files.exists(path)) {
            Files.createFile(path);
            return new Config(path);
        }
        Config c = gson.fromJson(Files.readString(path), Config.class);
        c.path = path;
        return c;
    }

    public String getGCCExe() {
        if(SysInfo.getOS() == SysInfo.OS.WIN) {
            return gcc_path + File.separator + "gcc.exe";

        }else {
            return gcc_path + "/gcc";
        }
    }

    public String getGPPExe() {
        if(SysInfo.getOS() == SysInfo.OS.WIN) {
            return gcc_path + File.separator + "g++.exe";

        }else {
            return gcc_path + "/g++";
        }
    }

    public void save() {
        try{
            String json = gson.toJson(this);
            Files.writeString(path, json);
        }catch(IOException e){
            e.printStackTrace();
        }

    }
}
