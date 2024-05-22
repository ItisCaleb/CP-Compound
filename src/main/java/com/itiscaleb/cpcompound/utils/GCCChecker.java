package com.itiscaleb.cpcompound.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GCCChecker {
    public static boolean isGCCInstalled() {
        try {
            Process process = new ProcessBuilder("gcc", "--version").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("gcc")) {
                    return true;
                }
            }
        } catch (IOException e) {
            // Handle exception if needed
        }
        return false;
    }

    public static void checkAndDownloadGCC() {
        if (!isGCCInstalled()) {
            System.out.println("GCC is not installed. Initiating download...");
            GCCDownloader downloader = new GCCDownloader();
            downloader.download();
        } else {
            System.out.println("GCC is already installed.");
        }
    }
}
