package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.component.EditableLabel;
import com.itiscaleb.cpcompound.component.TemplateItem;
import com.itiscaleb.cpcompound.utils.APPData;
import com.itiscaleb.cpcompound.utils.TemplateManager;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.dashicons.Dashicons;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

public class TemplateMainController {
    @FXML
    Button addCategoryBtn;
    @FXML
    VBox templateVBox;
    TemplateManager templateManager;
    Map<String, ArrayList<TemplateItem>> templates;
    @FXML
    private void handleAddCategory(){
        System.out.println("Add Category");

    }
    private void loadTemplatesUI(){
        templateVBox.getChildren().clear();
        templates = templateManager.getTemplates();
        for (Map.Entry<String, ArrayList<TemplateItem>> entry : templates.entrySet()) {
            String categoryName = entry.getKey();
            ArrayList<TemplateItem> items = entry.getValue();
            VBox categoryContent = new VBox(5); // Spacing between items
            categoryContent.getStyleClass().add("category-content");
            VBox contentArea = new VBox(5);
            contentArea.getStyleClass().add("contentArea");
            for (TemplateItem item : items) {
                VBox itemInfo = createItemInfoHBox(item);
                contentArea.getChildren().add(itemInfo);
            }
            HBox categoryHeader = createCategoryHeader(contentArea,categoryName);
            categoryContent.getChildren().addAll(categoryHeader,contentArea);
            templateVBox.getChildren().add(categoryContent);
        }
    }
    private HBox createCategoryHeader(VBox contentArea,String categoryName){
        ToggleButton collapseBtn = new ToggleButton();
        collapseBtn.setGraphic(new FontIcon());
        collapseBtn.getStyleClass().add("collapse-btn");
        collapseBtn.setSelected(true);
        contentArea.setVisible(false);
        contentArea.setManaged(false);
        FontIcon rightArrow = new FontIcon(Dashicons.ARROW_RIGHT_ALT2);
        FontIcon downArrow = new FontIcon(Dashicons.ARROW_DOWN_ALT2);
        collapseBtn.setGraphic(rightArrow);
        collapseBtn.setOnAction(event -> {
            if(collapseBtn.isSelected()){
                contentArea.setVisible(false);
                contentArea.setManaged(false);
                collapseBtn.setGraphic(rightArrow);
            }else{
                contentArea.setVisible(true);
                contentArea.setManaged(true);
                collapseBtn.setGraphic(downArrow);
            }
        });

        EditableLabel headerLabel = new EditableLabel();
        headerLabel.getStyleClass().add("header-label");
        headerLabel.setText(categoryName);


        Button addItemBtn = new Button();
        addItemBtn.setGraphic(new FontIcon());
        addItemBtn.getStyleClass().add("add-item-btn");
        addItemBtn.setOnAction(event -> handleAddItem());

        Button deleteItemBtn = new Button();
        deleteItemBtn.setGraphic(new FontIcon());
        deleteItemBtn.getStyleClass().add("delete-item-btn");

        deleteItemBtn.setOnAction(event -> handleDeleteCategory());

        HBox categoryHeader=new HBox(5);
        categoryHeader.getStyleClass().add("header-hbox");
        HBox headerBtnArea=new HBox(5);
        headerBtnArea.getStyleClass().add("header-btn-area");

        headerBtnArea.getChildren().addAll(addItemBtn,deleteItemBtn);
        headerBtnArea.setAlignment(Pos.CENTER_RIGHT);
        categoryHeader.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(headerBtnArea,Priority.ALWAYS);
        categoryHeader.getChildren().addAll(collapseBtn,headerLabel,headerBtnArea);
        HBox.setHgrow(headerLabel,Priority.ALWAYS);
        return categoryHeader;
    }
    private VBox createItemInfoHBox(TemplateItem item) {
        VBox itemInfo = new VBox(2); // Spacing between elements
        itemInfo.getStyleClass().add("item-info");
        HBox itemMainInfo = new HBox(10);
        itemMainInfo.getStyleClass().add("item-main-info");
        HBox itemSubInfo = new HBox(10);
        itemSubInfo.getStyleClass().add("item-sub-info");

        EditableLabel itemName = new EditableLabel();
        itemName.setText(item.getFileName());
        itemName.getStyleClass().add("item-name");

        HBox infoBtnArea =new HBox(5);
        infoBtnArea.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(infoBtnArea,Priority.ALWAYS);
        infoBtnArea.getStyleClass().add("info-btn-area");
        infoBtnArea.setTranslateY(8);
        Button openItemBtn = new Button();
        openItemBtn.setGraphic(new FontIcon());
        openItemBtn.getStyleClass().add("open-item-btn");
        openItemBtn.setOnAction(event -> handleOpenItem());

        Button removeItemBtn = new Button();
        removeItemBtn.setGraphic(new FontIcon());
        removeItemBtn.getStyleClass().add("remove-item-btn");
        removeItemBtn.setOnAction(event -> handleRemoveItem());

        infoBtnArea.getChildren().addAll(openItemBtn,removeItemBtn);

        itemMainInfo.getChildren().addAll(itemName,infoBtnArea);

        Label itemLineCount = new Label(String.valueOf(item.getLineCount())+" lines");
        itemLineCount.getStyleClass().add("item-line-count");

        Label fileExtension = new Label("type: "+item.getExtension());
        fileExtension.getStyleClass().add("file-extension");

        Label lastUpdateDate = new Label("edit "+item.getLastModified().toString());
        lastUpdateDate.getStyleClass().add("last-update-date");
        itemInfo.setOnMouseEntered(event -> {itemName.getLabel().setStyle("-fx-text-fill: #FFFFFF");});
        itemInfo.setOnMouseExited(event -> {itemName.getLabel().setStyle("-fx-text-fill: #A49F9F");});

        itemSubInfo.getChildren().addAll(itemLineCount,fileExtension, lastUpdateDate);
        itemInfo.getChildren().addAll(itemMainInfo, itemSubInfo);

        return itemInfo;
    }
    private void handleAddItem() {
        System.out.println("handleAddItem");
    }
    private void handleDeleteCategory(){
        System.out.println("handleDeleteCategory");
    }
    private void handleOpenItem() {
        System.out.println("Open Item");
    }

    private void handleRemoveItem() {
        System.out.println("handleremoveItem");
    }
    private void initTemplateManager() throws IOException{
        //build templateManager through "code template" folder
        templateManager = new TemplateManager();
        templateManager.displayTemplates();
    }
    private void initIcon(){
        addCategoryBtn.setGraphic(new FontIcon());
    }
    public void initialize() throws IOException {
        Path NewtemplatePath = APPData.resolve("Code Template");
        System.out.println("path: " + NewtemplatePath);
        if(!Files.exists(NewtemplatePath)) {
            System.out.println("creating folder: Code Template");
            Files.createDirectory(NewtemplatePath);
        }else{
            System.out.println("folder already exists");
        }
        initTemplateManager();
        loadTemplatesUI();
        initIcon();
    }


}
