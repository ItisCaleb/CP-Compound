package com.itiscaleb.cpcompound;

import com.itiscaleb.cpcompound.editor.Editor;
import com.itiscaleb.cpcompound.langServer.LSPProxy;
import com.itiscaleb.cpcompound.langServer.Language;
import com.itiscaleb.cpcompound.utils.Config;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;

public class CPCompound extends Application {
    static MainController mainController;
    static Config config;
    static HashMap<Language, LSPProxy> proxies = new HashMap<>();
    static Logger logger = LogManager.getLogger(CPCompound.class);
    static Editor editor;
    @Override
    public void start(Stage stage) throws IOException {
        initIDE();
        FXMLLoader fxmlLoader = new FXMLLoader(CPCompound.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        scene.getStylesheets().add(CPCompound.class.getResource("ide.css").toExternalForm());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
    public void initIDE() throws IOException {
        config = Config.load("./config.json");
        config.save();

        // init Language Server proxies
        LSPProxy clang = new LSPProxy(config.cpp_lang_server_path+"/bin/clangd" );
        proxies.put(Language.CPP, clang);
        proxies.put(Language.C, clang);
        clang.start();

        editor = new Editor();
        String key = editor.addContext("unnamed");
        editor.switchContext(key);
    }
    public static Config getConfig(){
        return config;
    }

    public static Logger getLogger(){
        return logger;
    }

    public static LSPProxy getLSPProxy(Language lang){
        return proxies.get(lang);
    }

    public static Editor getEditor(){
        return editor;
    }

    public static MainController getMainController(){
        return mainController;
    }

    public static void main(String[] args) {
        launch();
    }
}