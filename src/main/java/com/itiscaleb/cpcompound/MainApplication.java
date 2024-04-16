package com.itiscaleb.cpcompound;

import com.itiscaleb.cpcompound.editor.Editor;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    static MainController mainController;
    @Override
    public void start(Stage stage) throws IOException {
        initIDE();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
    public void initIDE(){
        Editor.getInstance().addContext("unnamed");
    }

    public static void main(String[] args) {
        launch();
    }
}