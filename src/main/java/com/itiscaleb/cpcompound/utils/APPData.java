package com.itiscaleb.cpcompound.utils;

import com.itiscaleb.cpcompound.CPCompound;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class APPData {
    static Path dataFolder;
    public static Path getDataFolder(){
        if (dataFolder == null) {
            switch (SysInfo.getOS()){
                case WIN -> dataFolder = Path.of(System.getenv("APPDATA") + "/CP-Compound");
                case MAC -> dataFolder =  Path.of(System.getProperty("user.home") + "/Library/Application Support/CP-Compound");
                case LINUX -> dataFolder = Path.of(System.getProperty("user.home") + "/.cp-compound");
                default -> throw new IllegalStateException("Unexpected value: " + SysInfo.getOS());
            }
            if(!dataFolder.toFile().exists()) {
                try {
                    Files.createDirectory(dataFolder);
                    Files.createDirectory(dataFolder.resolve("tmp"));
                } catch (IOException e) {
                    CPCompound.getLogger().error("Error occurred", e);
                }
            }
        }
        return dataFolder;
    }

    public static Path resolve(String path) {
        return getDataFolder().resolve(path);
    }

    public static Path resolve(Path path) {
        return getDataFolder().resolve(path);
    }
    
}
