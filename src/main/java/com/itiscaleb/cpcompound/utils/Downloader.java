package com.itiscaleb.cpcompound.utils;

import com.itiscaleb.cpcompound.CPCompound;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class Downloader {
    protected static Path downloadFromHTTP(String url) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Downloading \"" + url + "\"");
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET().build();
        File downloadDir = new File("./downloads");
        downloadDir.mkdir();
        HttpResponse<Path> res = client.send(req,
                HttpResponse.BodyHandlers
                        .ofFileDownload(downloadDir.getCanonicalFile().toPath(), CREATE, WRITE));
        CPCompound.getLogger().info(res.body());
        return res.body();
    }

    public void download() {}
}
