package com.itiscaleb.cpcompound.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TemplateContentController {
    @FXML
    ToggleButton collapseBtn;
    @FXML
    VBox contentArea;
    @FXML
    public void initialize() {

    }
    @FXML
    private void handleCollapse(){
        if(collapseBtn.isSelected()){
            contentArea.setVisible(false);
            contentArea.setManaged(false);
            collapseBtn.setText(">");
        }else{
            contentArea.setVisible(true);
            contentArea.setManaged(true);
            collapseBtn.setText("v");
        }
    }
    @FXML
    private void handleRemoveItem() {
        System.out.println("handleremoveItem");
    }
    @FXML
    private void handleDeleteCategory(){
        System.out.println("handleDeleteCategory");
    }
    @FXML
    private void handleOpenItem() {
        System.out.println("handleopenItem");
    }
    @FXML
    private void handleAddItem() {
        System.out.println("handleAddItem");
    }
}
