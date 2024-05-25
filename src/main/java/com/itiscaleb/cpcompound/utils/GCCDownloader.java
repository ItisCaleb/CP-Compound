package com.itiscaleb.cpcompound.utils;

import com.itiscaleb.cpcompound.CPCompound;
import javafx.beans.property.FloatProperty;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class GCCDownloader extends Downloader {
    private static final String GCC_WIN_URL = "https://sourceforge.net/projects/mingw-w64/files/latest/download";

    public static boolean isGCCInstalled() {
        try {
            Process p;
            if(SysInfo.getOS() == SysInfo.OS.WIN){
                p = Runtime.getRuntime().exec("where.exe gcc");
            }else {
                p = Runtime.getRuntime().exec("which gcc");
            }
            p.waitFor();
            if(p.exitValue() == 0){
                Config config = CPCompound.getConfig();
                config.gcc_downloaded = true;
                String path = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                config.gcc_path = path.substring(0, path.lastIndexOf("/"));
                config.save();
            }
            return p.exitValue() == 0;
        } catch (Exception e) {
            // Handle exception if needed
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void download() {
        try {
            if (Objects.requireNonNull(SysInfo.getOS()) == SysInfo.OS.WIN) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<Void> downloadAsync(FloatProperty progress) {
        return CompletableFuture.runAsync(()->{
            try{
                Path path = Downloader.progressDownloadFromHTTP(GCC_WIN_URL, progress);

                // unzip
                String installPath = "./installed/" + Utils.unzipFolder(path, "./installed");

                Config config = CPCompound.getConfig();
                config.gcc_path = installPath;
                config.gcc_downloaded = true;
                config.save();
                CPCompound.getLogger().info("GCC installed at: " + installPath);
            }catch (Exception e){

            }
        });
    }
}

