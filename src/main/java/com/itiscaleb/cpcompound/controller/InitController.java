package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.utils.ClangdDownloader;
import com.itiscaleb.cpcompound.utils.Config;
import javafx.fxml.FXML;

public class InitController {
    @FXML
    protected void downloadClangd(){
        new ClangdDownloader().download();
        Config config = CPCompound.getConfig();
        config.inited = true;
        config.save();
    }
}
