package com.itiscaleb.cpcompound.downloader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.utils.Config;
import com.itiscaleb.cpcompound.utils.SysInfo;
import com.itiscaleb.cpcompound.utils.Utils;
import javafx.beans.property.FloatProperty;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ClangdDownloader extends Downloader {

    String getClangdURL(){
        try{
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI("https://api.github.com/repos/clangd/clangd/releases/latest"))
                    .GET().build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            JsonObject json = new Gson().fromJson(res.body(), JsonObject.class);
            SysInfo.OS os = SysInfo.getOS();
            String arch = SysInfo.getArch();
            if(arch.equals("x86_64") || os == SysInfo.OS.WIN ||
                    (arch.equals("arm64") && os == SysInfo.OS.MAC)){
                String substr = "clangd-" + os.name;
                JsonArray arr = json.get("assets").getAsJsonArray();
                for (JsonElement elem : arr) {
                    if(elem.getAsJsonObject().get("name").getAsString().contains(substr)){
                        return elem.getAsJsonObject().get("browser_download_url").getAsString();
                    }
                }
            }
            return null;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public void download() {
        try{
            Path path = Downloader.downloadFromHTTP(getClangdURL());
            CPCompound.getConfig().cpp_lang_server_path = "./installed/"+ Utils.unzipFolder(path, "./installed");
            CPCompound.getConfig().save();
            CPCompound.getLogger().info(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public CompletableFuture<Void> downloadAsync(FloatProperty progress) {
        return CompletableFuture.runAsync(() -> {
            try {
                Path path = Downloader.progressDownloadFromHTTP(getClangdURL(),progress);
                Config config = CPCompound.getConfig();
                config.cpp_lang_server_path = "./installed/"+ Utils.unzipFolder(path, "./installed");
                config.lang_server_downloaded = true;
                config.save();
                if(SysInfo.getOS() != SysInfo.OS.WIN){
                    File f= new File(config.cpp_lang_server_path + "/bin/clangd");
                    f.setExecutable(true);
                }

                CPCompound.getLogger().info(path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
