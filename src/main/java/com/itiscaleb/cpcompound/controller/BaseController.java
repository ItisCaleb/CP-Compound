package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.component.EditorPopup;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.PopupWindow;
import javafx.stage.Window;

public class BaseController {

    static BaseController instance;

    @FXML
    AnchorPane appBase;

    @FXML
    SplitPane mainSplitPane, rightSplitPane;

    @FXML
    EditorPopup messagePopup;


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



    private void loadRightPane(){
        rightSplitPane = new SplitPane();
        rightSplitPane.setDividerPositions(0.8);
        rightSplitPane.setOrientation(Orientation.VERTICAL);
        this.mainSplitPane.getItems().add(rightSplitPane);
        loadEditor();
        loadConsole();
    }
    private void loadEditor(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CPCompound.class.getResource("fxml/editor.fxml"));
            TabPane editorTabPane = fxmlLoader.load();
            rightSplitPane.getItems().add(editorTabPane);
        } catch (Exception e) {
            CPCompound.getLogger().error("Error occurred", e);
        }
    }

    private void loadConsole(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CPCompound.class.getResource("fxml/console.fxml"));
            SplitPane editorTabPane = fxmlLoader.load();
            rightSplitPane.getItems().add(editorTabPane);
        } catch (Exception e) {
            CPCompound.getLogger().error("Error occurred", e);
        }
    }



    @FXML
    public void initialize() {
            instance = this;
            this.messagePopup = new EditorPopup(4);
            this.messagePopup.setAutoHide(true);
            this.messagePopup.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_RIGHT);
            loadToolBar();
            loadMenuBar();
            loadFunctionPane();
            loadRightPane();
            CPCompound.getLogger().info("initialize App Base");
    }

    public static BaseController getInstance() {
        return instance;
    }

    public void showMessageToUser(String str){
        messagePopup.setText(str);
        Window window = appBase.getScene().getWindow();
        messagePopup.show(appBase, window.getX() + window.getWidth() - 5,
                window.getY() + window.getHeight() - 10);
    }


}
