package com.itiscaleb.cpcompound;

import com.itiscaleb.cpcompound.controller.BaseController;
import com.itiscaleb.cpcompound.controller.ConsoleController;
import com.itiscaleb.cpcompound.controller.FileTreeViewController;
import com.itiscaleb.cpcompound.editor.Editor;
import com.itiscaleb.cpcompound.editor.EditorContext;
import com.itiscaleb.cpcompound.langServer.LSPProxy;
import com.itiscaleb.cpcompound.langServer.Language;
import com.itiscaleb.cpcompound.langServer.c.CPPSemanticTokenType;
import com.itiscaleb.cpcompound.utils.APPData;
import com.itiscaleb.cpcompound.utils.Config;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.ServerCapabilities;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class CPCompound extends Application {
    static BaseController baseController;
    static Config config;
    static HashMap<Language, LSPProxy> proxies = new HashMap<>();
    static Logger logger;
    static Editor editor;
    static Stage primaryStage;


    @Override
    public void start(Stage primaryStage) throws IOException {
        System.setProperty("logfile.name", APPData.resolve("latest.log").toString());
        logger =  LogManager.getLogger("CPCompound");
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
            editor.stopExecute();
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
            Platform.runLater(CPCompound::afterInit);
        }catch (IOException e){
            CPCompound.getLogger().error("Error occurred", e);
        }

    }


    private static void initIDE() {
        String clangQueryDriver = "--query-driver="+config.getGCCExe()+","+config.getGPPExe();
        String compileCommandsDir = "--compile-commands-dir="+APPData.getDataFolder();
        // init Language Server proxies
        LSPProxy clang = new LSPProxy(config.cpp_lang_server_path+"/bin/clangd"
                , clangQueryDriver, compileCommandsDir);
        proxies.put(Language.CPP, clang);
        proxies.put(Language.C, clang);
        LSPProxy mock = new LSPProxy("");
        proxies.put(Language.Python, mock);
        proxies.put(Language.None, mock);
        ServerCapabilities capabilities = clang.start();
        CPPSemanticTokenType.init(capabilities.getSemanticTokensProvider().getLegend().getTokenTypes());
        editor = new Editor();
    }

    private static void afterInit(){
        String lastDir = config.last_open_directory;
        if(lastDir != null && !lastDir.isEmpty()){
            File f = new File(lastDir);
            if(f.exists()){
                FileTreeViewController.getInstance().loadDirectoryIntoTreeView(f);
            }
        }
        ConsoleController.getInstance().logToUser("Welcome to CP Compound");
    }


    public static Config getConfig(){
        return config;
    }

    public static Logger getLogger(){
        return logger;
    }

    public static LSPProxy getLSPProxy(EditorContext context){
        return proxies.get(context.getLang());
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