package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.component.EditorPopup;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BaseController {




    @FXML
    AnchorPane appBase;

    @FXML
    SplitPane mainSplitPane;

    @FXML
    EditorPopup messagePopup = new EditorPopup();


    private void loadToolBar(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CPCompound.class.getResource("fxml/tool-bar.fxml"));
            HBox toolBar = fxmlLoader.load();
            appBase.getChildren().add(toolBar);
        } catch (Exception e) {
            CPCompound.getLogger().error("Error occurred", e);
        }
    }
    private void loadMenuBar(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CPCompound.class.getResource("fxml/menu-bar.fxml"));
            VBox menuBar = fxmlLoader.load();
            appBase.getChildren().add(menuBar);

        } catch (Exception e) {
            CPCompound.getLogger().error("Error occurred", e);
        }
    }

    private void loadFunctionPane(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CPCompound.class.getResource("fxml/function-pane.fxml"));
            VBox editorFunctionPane = fxmlLoader.load();
            mainSplitPane.getItems().add(editorFunctionPane);
            FunctionPaneController functionPaneController = fxmlLoader.getController();
            functionPaneController.setCurrentActiveMenuItem(MenuBarController.getInstance().getCurrentActiveMenuItem());
        } catch (Exception e) {
            CPCompound.getLogger().error("Error occurred", e);
        }
    }

    private void loadEditor(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CPCompound.class.getResource("fxml/editor.fxml"));
            TabPane editorTabPane = fxmlLoader.load();
            mainSplitPane.getItems().add(editorTabPane);
        } catch (Exception e) {
            CPCompound.getLogger().error("Error occurred", e);
        }
    }


    @FXML
    public void initialize() {
            loadToolBar();
            loadMenuBar();
            loadFunctionPane();
            loadEditor();
            CPCompound.getLogger().info("initialize App Base");
    }

    public void showMessageToUser(String str){
        messagePopup.setText(str);
        messagePopup.show(appBase, appBase.getBoundsInLocal().getWidth() - 50, appBase.getHeight() - 30);
    }


}
