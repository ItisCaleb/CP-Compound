package com.itiscaleb.cpcompound;

import com.itiscaleb.cpcompound.controller.BaseController;
import com.itiscaleb.cpcompound.editor.Editor;
import com.itiscaleb.cpcompound.langServer.LSPProxy;
import com.itiscaleb.cpcompound.langServer.Language;
import com.itiscaleb.cpcompound.utils.APPData;
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
    static BaseController baseController;
    static Config config;
    static HashMap<Language, LSPProxy> proxies = new HashMap<>();
    static Logger logger = LogManager.getLogger(CPCompound.class);
    static Editor editor;
    static Stage primaryStage;


    @Override
    public void start(Stage primaryStage) throws IOException {

        config = Config.load(APPData.resolve("config.json"));
        config.save();
        CPCompound.primaryStage = primaryStage;
        CPCompound.primaryStage.initStyle(StageStyle.DECORATED);
        if(!config.lang_server_downloaded || !config.gcc_downloaded){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/init.fxml"));
            primaryStage.setScene(new Scene(fxmlLoader.load(), 800, 600));
        }else {
            setIDEStage();
        }
        primaryStage.setOnCloseRequest((event)->{
            for (var proxy: proxies.values()){
                proxy.stop();
            }
            System.exit(0);
        });
        primaryStage.setTitle("CP Compound");
        primaryStage.show();
    }

    public static void setIDEStage() {
        try {
            initIDE();
            UserAgentBuilder.builder()
                    .themes(JavaFXThemes.MODENA)
                    .themes(MaterialFXStylesheets.forAssemble(true))
                    .setDeploy(true)
                    .setResolveAssets(true)
                    .build()
                    .setGlobal();


            primaryStage.setMinWidth(1098);
            primaryStage.setMinHeight(700);
            FXMLLoader fxmlLoader = new FXMLLoader(CPCompound.class.getResource("fxml/app-base.fxml"));
            Parent editorRoot = fxmlLoader.load();
            baseController = fxmlLoader.getController();
            Scene scene = new Scene(editorRoot);
            primaryStage.setScene(scene);
            primaryStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }

    }


    public static void initIDE() {

        // init Language Server proxies
        LSPProxy clang = new LSPProxy(config.cpp_lang_server_path+"/bin/clangd"
                , "--query-driver="+config.gcc_path+"/gcc");
        proxies.put(Language.CPP, clang);
        proxies.put(Language.C, clang);
        clang.start();

        editor = new Editor();
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

    public static Stage getStage(){
        return primaryStage;
    }

    public static BaseController getBaseController(){
        return baseController;
    }

    public static void main(String[] args) {
        launch();
    }
}