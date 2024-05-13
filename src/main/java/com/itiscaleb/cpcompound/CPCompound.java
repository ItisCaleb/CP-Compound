package com.itiscaleb.cpcompound;

import com.itiscaleb.cpcompound.controller.MainEditorController;
import com.itiscaleb.cpcompound.editor.Editor;
import com.itiscaleb.cpcompound.langServer.LSPProxy;
import com.itiscaleb.cpcompound.utils.Config;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;

public class CPCompound extends Application {
    static MainController mainController;
    static Config config;
    static LSPProxy proxy;
    static Logger logger = LogManager.getLogger(CPCompound.class);
    static Editor editor;
    @Override
    public void start(Stage primaryStage) throws IOException {

        initIDE();

        UserAgentBuilder.builder()
                .themes(JavaFXThemes.MODENA)
                .themes(MaterialFXStylesheets.forAssemble(true))
                .setDeploy(true)
                .setResolveAssets(true)
                .build()
                .setGlobal();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/editor-main.fxml"));
        fxmlLoader.setController(new MainEditorController());
        Parent editorRoot = fxmlLoader.load();
        Scene scene = new Scene(editorRoot);
//        scene.getStylesheets().add(CPCompound.class.getResource("ide.css").toExternalForm());
        primaryStage.setFullScreen(true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("CP Compound");
        primaryStage.show();
    }
    public void initIDE() throws IOException {
        config = Config.load("./config.json");
        config.save();
        proxy = new LSPProxy("c++", config.cpp_lang_server_path+"/bin/clangd");
        proxy.start();
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

    public static LSPProxy getLSPProxy(){
        return proxy;
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