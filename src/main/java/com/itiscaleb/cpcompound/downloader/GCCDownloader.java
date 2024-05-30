package com.itiscaleb.cpcompound.downloader;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.utils.Config;
import com.itiscaleb.cpcompound.utils.SysInfo;
import com.itiscaleb.cpcompound.utils.Utils;
import javafx.beans.property.FloatProperty;
import javafx.scene.text.Text;
import net.sf.sevenzipjbinding.SevenZip;

import java.io.File;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class GCCDownloader extends Downloader {
    private static final String GCC_WIN_URL = "https://github.com/brechtsanders/winlibs_mingw/releases/download/14.1.0posix-18.1.5-11.0.1-ucrt-r1/winlibs-x86_64-posix-seh-gcc-14.1.0-mingw-w64ucrt-11.0.1-r1.7z";

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
                config.gcc_path = path.substring(0, path.lastIndexOf(File.separator));
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
    public CompletableFuture<Void> downloadAsync(FloatProperty progress, Text text) {
        return CompletableFuture.runAsync(()->{
            try{
                if(SysInfo.getArch().contains("x86")){
                    System.load(CPCompound.class.getResource("/sevenzip/Windows-x86/lib7-Zip-JBinding.dll").getFile());
                    SevenZip.initLoadedLibraries();
                }else if(SysInfo.getArch().contains("amd64")) {
                    System.load(CPCompound.class.getResource("/sevenzip/Windows-amd64/lib7-Zip-JBinding.dll").getFile());
                    SevenZip.initLoadedLibraries();
                }
                if(text != null){
                    text.setText("Downloading GCC...");
                }
                Path path = Downloader.progressDownloadFromHTTP(GCC_WIN_URL, progress);
                if(text != null){
                    text.setText("Unzipping GCC...");
                }
                // unzip
                String installPath = "./installed/" + Utils.unzip7z(path, "./installed");

                Config config = CPCompound.getConfig();
                config.gcc_path = installPath;
                config.gcc_downloaded = true;
                config.save();
                CPCompound.getLogger().info("GCC installed at: " + installPath);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
}

