package com.itiscaleb.cpcompound;

import com.itiscaleb.cpcompound.controller.MainEditorController;
import com.itiscaleb.cpcompound.editor.Editor;
import com.itiscaleb.cpcompound.langServer.LSPProxy;
import com.itiscaleb.cpcompound.langServer.Language;
import com.itiscaleb.cpcompound.utils.Config;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.util.HashMap;

public class CPCompound extends Application {
    static MainEditorController mainEditorController;
    static Config config;
    static HashMap<Language, LSPProxy> proxies = new HashMap<>();
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


        primaryStage.initStyle(StageStyle.DECORATED);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/editor-main.fxml"));
        mainEditorController = fxmlLoader.getController();
        fxmlLoader.setController(mainEditorController);
        Parent editorRoot = fxmlLoader.load();
        Scene scene = new Scene(editorRoot);
//        primaryStage.setFullScreen(true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("CP Compound");
        primaryStage.show();
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

//    public static MainController getMainController(){
//        return mainController;
//    }

    public static void main(String[] args) {
        launch();
    }
}