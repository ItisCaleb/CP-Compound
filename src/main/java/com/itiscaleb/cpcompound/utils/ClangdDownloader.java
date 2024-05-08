package com.itiscaleb.cpcompound.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itiscaleb.cpcompound.CPCompound;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

public class ClangdDownloader extends Downloader {
    @Override
    public void download() {
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
                        String url = elem.getAsJsonObject().get("browser_download_url").getAsString();
                        if(!CPCompound.getConfig().cpp_lang_server_path.isEmpty()) break;
                        Path path = Downloader.downloadFromHTTP(url);
                        CPCompound.getConfig().cpp_lang_server_path = "./installed/"+ Utils.unzipFolder(path, "./installed");
                        CPCompound.getConfig().save();
                        CPCompound.getLogger().info(path);
                        break;
                    }
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
