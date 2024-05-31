package com.itiscaleb.cpcompound.component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class EditableLabel extends StackPane {
    private Label label = new Label();
    private TextField textField = new TextField();

    public EditableLabel() {
        this("ssss");
    }

    public EditableLabel(String initialText) {
        // 初始化Label和TextField
        super();
        label.setText(initialText);
        textField.setText(initialText);
        textField.setVisible(false);

        this.getChildren().addAll(label, textField);
        this.getStyleClass().add("editable-label");
        setupInteractions();
    }

    private void setupInteractions() {
        label.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                showTextField();
            }
        });

        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                label.setText(textField.getText());
                showLabel();
            }
        });

        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                label.setText(textField.getText());
                showLabel();
            }
        });
    }

    private void showTextField() {
        label.setVisible(false);
        textField.setVisible(true);
        textField.requestFocus();
        textField.selectAll();
    }

    private void showLabel() {
        label.setVisible(true);
        textField.setVisible(false);
    }

    public String getText() {
        return label.getText();
    }

    @FXML
    public void setText(String text) {
        label.setText(text);
        textField.setText(text);
    }
}