package com.itiscaleb.cpcompound;

import com.itiscaleb.cpcompound.editor.Editor;
import com.itiscaleb.cpcompound.editor.EditorContext;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;

public class MainController {
    @FXML
    private Label welcomeText;

    @FXML
    private TextArea editorTextArea;

    public void initialize(){
        EditorContext context = Editor.getInstance().getCurrentContext();
        editorTextArea.textProperty().bindBidirectional(context.getStringProperty());
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onEditorInput(){
        EditorContext context = Editor.getInstance().getCurrentContext();
        System.out.println(context.getCode());
    }
}