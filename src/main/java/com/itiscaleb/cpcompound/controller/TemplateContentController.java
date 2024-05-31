package com.itiscaleb.cpcompound.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TemplateContentController {

    @FXML
    private VBox mainContainer;
    @FXML
    private HBox header;
    @FXML
    private VBox contentArea;
    @FXML
    private Button toggleButton;

    @FXML
    public void initialize() {
        toggleButton.setOnAction(event -> toggleContent());
    }

    private void toggleContent() {
        contentArea.setVisible(!contentArea.isVisible());
        toggleButton.setText(contentArea.isVisible() ? "v" : ">");
    }

    @FXML
    private void handleRemoveItem() {
        System.out.println("removeItem");
        // Implement adding items to contentArea or a specific list inside it
    }
}
