package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.component.EditorPopup;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;
import javafx.util.Duration;

public class BaseController {
    public EditorController getEditorController() {
        return editorController;
    }

    static EditorController editorController;

    @FXML
    AnchorPane appBase;

    @FXML
    EditorPopup messagePopup = new EditorPopup();

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

    public void showMessageToUser(String str){
        messagePopup.setText(str);
        messagePopup.show(appBase, appBase.getBoundsInLocal().getWidth() - 50, appBase.getHeight() - 30);
    }
}
