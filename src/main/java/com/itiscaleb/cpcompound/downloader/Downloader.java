package com.itiscaleb.cpcompound.downloader;

import com.itiscaleb.cpcompound.CPCompound;
import javafx.beans.property.FloatProperty;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;
import java.util.function.LongConsumer;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public abstract class Downloader {
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

    protected static Path progressDownloadFromHTTP(String url, FloatProperty progress) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Downloading \"" + url + "\"");
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        HttpRequest head = HttpRequest.newBuilder()
                .uri(new URI(url)).method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        var header = client.send(head, HttpResponse.BodyHandlers.discarding()).headers();
        int length = header.firstValue("Content-Length").map(Integer::parseInt).orElse(0);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET().build();
        File downloadDir = new File("./downloads");
        downloadDir.mkdir();
        var handler = progressBodyHandler(1024*1024, (bytes)->{
                progress.set((float)bytes/length);
            },HttpResponse.BodyHandlers
                .ofFileDownload(downloadDir.getCanonicalFile().toPath(), CREATE, WRITE));

        var res = client.send(req, handler);
        CPCompound.getLogger().info(res);
        return res.body();
    }

    abstract public void download();

    abstract public CompletableFuture<Void> downloadAsync(FloatProperty progress);

    private static <T> HttpResponse.BodyHandler<T> progressBodyHandler(int interval, LongConsumer callback, HttpResponse.BodyHandler<T> h) {
        return info -> new HttpResponse.BodySubscriber<T>() {
            private HttpResponse.BodySubscriber<T> delegateSubscriber = h.apply(info);
            private long receivedBytes = 0;
            private long calledBytes = 0;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                delegateSubscriber.onSubscribe(subscription);
            }

            @Override
            public void onNext(List<ByteBuffer> item) {
                receivedBytes += item.stream().mapToLong(ByteBuffer::capacity).sum();

                if (receivedBytes - calledBytes > interval) {
                    callback.accept(receivedBytes);
                    calledBytes = receivedBytes;
                }

                delegateSubscriber.onNext(item);
            }

            @Override
            public void onError(Throwable throwable) {
                delegateSubscriber.onError(throwable);

            }

            @Override
            public void onComplete() {
                delegateSubscriber.onComplete();
            }

            @Override
            public CompletionStage<T> getBody() {
                return delegateSubscriber.getBody();
            }
        };
    }
}
