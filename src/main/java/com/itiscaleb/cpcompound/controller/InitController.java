package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.downloader.ClangdDownloader;
import com.itiscaleb.cpcompound.utils.Config;
import com.itiscaleb.cpcompound.downloader.GCCDownloader;
import com.itiscaleb.cpcompound.utils.SysInfo;
import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;
import net.sf.sevenzipjbinding.SevenZip;

public class InitController {

    @FXML
    ProgressBar downloadProgress;

    @FXML
    ProgressIndicator downloadIndicator;

    @FXML
    Text downloadText;

    FloatProperty progressProperty = new SimpleFloatProperty();

    @FXML
    void initialize() {
        downloadProgress.setProgress(0);
        downloadIndicator.setProgress(0);
        progressProperty.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                downloadProgress.setProgress(newValue.doubleValue());
                downloadIndicator.setProgress(newValue.doubleValue());
            });
        });
        Config config = CPCompound.getConfig();
        if(!config.lang_server_downloaded) downloadClangd();
        else if(!config.gcc_downloaded) downloadGCC();
    }

    protected void downloadClangd() {
        downloadText.setText("Downloading Clangd...");
        new ClangdDownloader()
                .downloadAsync(progressProperty)
                .whenComplete((unused, throwable) -> downloadGCC());
    }

    protected void downloadGCC() {
        if(!GCCDownloader.isGCCInstalled()) {
            switch (SysInfo.getOS()) {
                case WIN -> {
                    downloadText.setText("Downloading GCC...");
                    new GCCDownloader()
                        .downloadAsync(progressProperty)
                        .whenComplete((unused, throwable) -> {
                            throwable.printStackTrace();
                            Platform.runLater(CPCompound::setIDEStage);
                        });
                }
                case LINUX -> {
                    downloadProgress.setVisible(false);
                    downloadIndicator.setVisible(false);
                    downloadText.setText("Type the following command in terminal\nsudo apt-get install gcc");
                }
                case MAC -> {
                    downloadProgress.setVisible(false);
                    downloadIndicator.setVisible(false);
                    downloadText.setText("Download 'brew' then type following command in terminal\nbrew install gcc");
                }
            }

        }else Platform.runLater(CPCompound::setIDEStage);
        downloadText.setText("Downloading GCC...");
    }
}
