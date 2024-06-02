package com.itiscaleb.cpcompound.component;

import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;


public class EditorPopup extends Popup {

    PauseTransition timer = new PauseTransition(Duration.seconds(3));
    static final String labelStyle =
            "-fx-background-color: black;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 5";

    Label label;
    public EditorPopup(){
        super();
        label = new Label();
        label.setStyle(labelStyle);
        this.getContent().add(label);
        timer.setOnFinished(event -> {
            System.out.println(label.isFocused());
            if(label.isFocused()){
               timer.playFromStart();
            }else this.hide();
        });
    }

    public void setText(String str){
        label.setText(str);
        timer.playFromStart();
    }

}
