package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.utils.ClangdDownloader;
import javafx.fxml.FXML;

public class InitController {
    @FXML
    protected void downloadClangd(){
        new ClangdDownloader().download();
    }
}
