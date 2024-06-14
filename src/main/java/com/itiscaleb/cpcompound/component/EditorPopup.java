package com.itiscaleb.cpcompound.component;

import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;


public class EditorPopup extends Popup {

    PauseTransition timer;
    static final String labelStyle =
            "-fx-background-color: black;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 5";

    Label label;
    boolean should_hide = true;


    public EditorPopup() {
        this(0.5);
    }


    public EditorPopup(double hideSeconds){
        super();
        timer = new PauseTransition(Duration.seconds(hideSeconds));
        // make label copyable
        label = new Label();
        label.setStyle(labelStyle);
        this.getContent().add(label);
        label.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            should_hide = false;
        });

        label.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            should_hide = true;
            timer.playFromStart();
        });
        timer.setOnFinished(event -> {
            if(this.should_hide){
               super.hide();
            }
        });
    }

    public void setText(String str){
        label.setText(str);
    }

    // If autoHide is true, then this popup will hide seconds after lost hover
    @Override
    public void show(Node ownerNode, double anchorX, double anchorY) {
        super.show(ownerNode, anchorX, anchorY);
        timer.pause();
        if(this.isAutoHide()){
            hide();
        }
    }

    // If autoHide is true, then this popup will hide seconds after lost hover
    @Override
    public void show(Window ownerWindow, double anchorX, double anchorY) {
        super.show(ownerWindow, anchorX, anchorY);
        timer.pause();
        if(this.isAutoHide()){
            hide();
        }
    }

    // Hide this popup seconds after lost hover
    @Override
    public void hide() {
        this.should_hide = true;
        timer.playFromStart();
    }
}
