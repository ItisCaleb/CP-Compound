package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.CPCompound;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;

public class BaseController {
    public EditorController getEditorController() {
        return editorController;
    }

    static EditorController editorController;


    @FXML
    AnchorPane appBase;

    private void loadEditor(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CPCompound.class.getResource("fxml/editor.fxml"));
            SplitPane  editorSplitPane = fxmlLoader.load();
            appBase.getChildren().add(editorSplitPane);
            editorController = fxmlLoader.getController();
        } catch (Exception e) {
            CPCompound.getLogger().error("Error occurred", e);
        }
    }
    @FXML
    public void initialize() {
            loadEditor();
            CPCompound.getLogger().info("initialize App Base");
    }
}
