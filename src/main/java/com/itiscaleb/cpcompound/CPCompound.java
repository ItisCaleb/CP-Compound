package com.itiscaleb.cpcompound;

import com.itiscaleb.cpcompound.controller.InitController;
import com.itiscaleb.cpcompound.controller.MainEditorController;
import com.itiscaleb.cpcompound.editor.Editor;
import com.itiscaleb.cpcompound.langServer.LSPProxy;
import com.itiscaleb.cpcompound.langServer.Language;
import com.itiscaleb.cpcompound.utils.Config;
import com.itiscaleb.cpcompound.utils.GCCChecker;
import com.itiscaleb.cpcompound.utils.GCCDownloader;
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class CPCompound extends Application {
    static MainEditorController mainEditorController;
    static Config config;
    static HashMap<Language, LSPProxy> proxies = new HashMap<>();
    static Logger logger = LogManager.getLogger(CPCompound.class);
    static Editor editor;

    private void testDownload() {
        // 測試下載過程
        System.out.println("Initiating GCC download test...");
        GCCDownloader downloader = new GCCDownloader();
        downloader.download();

        // 檢查下載是否成功
        // 這裡假設下載後的文件會存放在預期的目錄下，你可以根據實際情況修改路徑
        File downloadedFile = new File("./installed/gcc-download.zip");
        if (downloadedFile.exists()) {
            System.out.println("GCC download test successful!");
        } else {
            System.out.println("GCC download test failed! Downloaded file not found.");
        }

        // 檢查安裝位置是否正確
        // 這裡假設下載的 GCC 文件會解壓並安裝到指定目錄下，你可以根據實際情況修改路徑
        File installedGCC = new File("./installed/gcc");
        if (installedGCC.exists()) {
            System.out.println("GCC installed at: " + installedGCC.getAbsolutePath());
        } else {
            System.out.println("GCC installation test failed! GCC directory not found.");
        }
    }


    @Override
    public void start(Stage primaryStage) throws IOException {

        GCCChecker gccChecker = new GCCChecker();
        gccChecker.checkAndDownloadGCC();

        //testDownload();

        config = Config.load("./config.json");
        config.save();

        if(!config.inited){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/init.fxml"));
            primaryStage.setScene(new Scene(fxmlLoader.load(), 800, 600));
        }else {
            initIDE();
            UserAgentBuilder.builder()
                    .themes(JavaFXThemes.MODENA)
                    .themes(MaterialFXStylesheets.forAssemble(true))
                    .setDeploy(true)
                    .setResolveAssets(true)
                    .build()
                    .setGlobal();


            primaryStage.initStyle(StageStyle.DECORATED);
            primaryStage.setMinWidth(1098);
            primaryStage.setMinHeight(700);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/editor-main.fxml"));
            mainEditorController = fxmlLoader.getController();
            fxmlLoader.setController(mainEditorController);
            Parent editorRoot = fxmlLoader.load();
            Scene scene = new Scene(editorRoot);
//        primaryStage.setFullScreen(true);
            primaryStage.setScene(scene);
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
    public void initIDE() throws IOException {


        // init Language Server proxies
        LSPProxy clang = new LSPProxy(config.cpp_lang_server_path+"/bin/clangd" );
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

//    public static MainController getMainController(){
//        return mainController;
//    }

    public static void main(String[] args) {
        launch();
    }
}