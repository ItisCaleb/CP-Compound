package com.itiscaleb.cpcompound.utils;

import com.itiscaleb.cpcompound.CPCompound;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GCCDownloader extends Downloader {
    private static final String GCC_WIN_URL = "https://sourceforge.net/projects/mingw-w64/files/latest/download";

    @Override
    public void download() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                System.out.print("windows\n");
                downloadForWindows();
            } else if (os.contains("mac")) {
                installForMac();
            } else if (os.contains("nix") || os.contains("nux")) {
                installForLinux();
            } else {
                System.out.println("Unsupported operating system for automatic GCC download.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadForWindows() throws IOException {
        String url = GCC_WIN_URL;
        Path outputPath = Paths.get("./installed/gcc-download.zip");
        downloadFromURL(url, outputPath);

        System.out.print("outputPath : ");
        System.out.print(outputPath);
        // 解壓縮文件到指定目錄
        String installPath = "./installed/" + Utils.unzipFolder(outputPath, "./installed");
        //CPCompound.getConfig().gcc_install_path = installPath;
        CPCompound.getConfig().save();
        CPCompound.getLogger().info("GCC installed at: " + installPath);
    }

    private void installForMac() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", "brew install gcc"});
            process.waitFor();
            CPCompound.getLogger().info("GCC installed using Homebrew.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void installForLinux() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", "sudo apt-get install gcc -y"});
            process.waitFor();
            CPCompound.getLogger().info("GCC installed using apt-get.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void downloadFromURL(String urlStr, Path outputPath) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.addRequestProperty("User-Agent", "Mozilla/4.76");
        try (BufferedInputStream in = new BufferedInputStream(httpConn.getInputStream());
             FileOutputStream fileOutputStream = new FileOutputStream(outputPath.toFile())) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }
}

