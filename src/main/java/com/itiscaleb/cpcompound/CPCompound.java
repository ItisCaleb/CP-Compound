package com.itiscaleb.cpcompound;

import com.itiscaleb.cpcompound.editor.Editor;
import com.itiscaleb.cpcompound.utils.Config;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CPCompound extends Application {
    static MainController mainController;
    static Config config;
    @Override
    public void start(Stage stage) throws IOException {
        initIDE();
        FXMLLoader fxmlLoader = new FXMLLoader(CPCompound.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
    public void initIDE() throws IOException {
        config = Config.load("./config.json");
        config.save();
        Editor.getInstance().addContext("unnamed");
    }
    public static Config getConfig(){
        return config;
    }

    public static void main(String[] args) {
        launch();
    }
}