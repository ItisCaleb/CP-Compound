package com.itiscaleb.cpcompound.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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

    public void checkAndDownloadGCC() throws IOException {
        if (isGCCInstalled()) {
            System.out.println("GCC is not installed. Initiating download...");
//            FXMLLoader fxmlLoader = new FXMLLoader();
            AnchorPane root =  FXMLLoader.load(getClass().getResource("/com/itiscaleb/cpcompound/fxml/gcc-checker-pane.fxml"));
            Stage gccStage = new Stage();
            gccStage.setTitle("GCC Checker");
            gccStage.setScene(new Scene(root));
            gccStage.show();
//            GCCDownloader downloader = new GCCDownloader();
//            downloader.download();
        } else {
            System.out.println("GCC is already installed.");
        }
    }
}
