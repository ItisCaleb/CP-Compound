package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.CPCompound;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.HashMap;
import java.util.Map;

public class FunctionPaneController {
    static FunctionPaneController instance;
    @FXML
    Button functionTabButton;
    @FXML
    StackPane functionPaneContentArea;

    static CheckerController checkerController;

    public CheckerController getCheckerController() {
        return checkerController;
    }



    public void setCurrentActiveMenuItem(Button currentActiveMenuItem) {
        this.currentActiveMenuItem = currentActiveMenuItem;
    }

    public Button currentActiveMenuItem;
    public VBox  currentFunctionContent;
    public void assignFunctionTab(String itemId,Button sourceButton){
        //set unselected style
        currentActiveMenuItem.setStyle("-fx-background-color: transparent;");
        FontIcon itemIcon = (FontIcon) currentActiveMenuItem.getGraphic();
        itemIcon.setStyle(itemIcon.getStyle()+"-fx-icon-color: #CCCCCC;");
        //change to selected button
        currentActiveMenuItem = sourceButton;
        //set selected style
        currentActiveMenuItem.setStyle("-fx-background-color: #4a4b4e;");
        itemIcon = (FontIcon) currentActiveMenuItem.getGraphic();
        itemIcon.setStyle(itemIcon.getStyle()+"-fx-icon-color: #FFFFFF;");
        currentActiveMenuItem.setGraphic(itemIcon);
        MenuBarController.getInstance().setCurrentActiveMenuItem(currentActiveMenuItem);
        switch(itemId){
            case "File-button":
                loadContent("fxml/file-treeView.fxml");
                functionTabButton.setText("File View");
                break;
            case "Checker-button":
                loadContent("fxml/checker.fxml");
                functionTabButton.setText("Checker");
                break;
            case "Generator-button":
                loadContent("fxml/generator.fxml");
                functionTabButton.setText("Generator");
                break;
            case "Note-system-button":
                loadContent("fxml/note-system.fxml");
                functionTabButton.setText("Note System");
                break;
            case "Setting-button":
                loadContent("fxml/setting.fxml");
                functionTabButton.setText("Setting");
                break;
            default:
        }

    }

    //id,object
    final private Map<String,VBox> functionPaneCache = new HashMap<>();
    private void loadContent(String fxmlFile) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CPCompound.class.getResource(fxmlFile));
            VBox content = fxmlLoader.load();
            content.prefWidthProperty().bind(functionPaneContentArea.widthProperty());
            content.prefHeightProperty().bind(functionPaneContentArea.heightProperty());
            switch (fxmlFile){
                case "fxml/checker.fxml":
                    checkerController = fxmlLoader.getController();
                    break;
                default:
            }


            if(!content.getId().equals("terminal")){
                if(functionPaneCache.containsKey(content.getId())){
                    currentFunctionContent = functionPaneCache.get(content.getId());
                    functionPaneContentArea.getChildren().setAll(functionPaneCache.get(content.getId()));
                }else{
                    currentFunctionContent = content;
                    functionPaneContentArea.getChildren().setAll(content);
                    functionPaneCache.put(content.getId(),content);
                }
            }else{
                functionPaneContentArea.getChildren().setAll(content);
            }

        } catch (Exception e) {
            CPCompound.getLogger().error("Error occurred", e);
        }
    }
    private void initFunctonPane(String fxmlFilePath){
        loadContent(fxmlFilePath);
        this.currentActiveMenuItem = MenuBarController.getInstance().getCurrentActiveMenuItem();
        assignFunctionTab(fxmlFilePath,currentActiveMenuItem);
    }
    @FXML
    public void initialize() {
        instance = this;
        initFunctonPane("fxml/file-treeView.fxml");
        CPCompound.getLogger().info("initialize EditorFunctionPane");
    }

    public static FunctionPaneController getInstance() {
        return instance;
    }
}
