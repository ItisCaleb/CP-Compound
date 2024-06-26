package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.editor.Editor;
import com.itiscaleb.cpcompound.editor.EditorContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ToolBarController {
    static ToolBarController instance;
//    @FXML
//    ToolBar mainEditorToolBar;
    @FXML
    Button addFileBtn,compileBtn, templateBtn, searchIconBtn, replaceIconBtn, helpIconBtn;//, minimizeWindowBtn, closeWindowBtn;
//    @FXML
//    ToggleButton adjustWindowBtn;
    @FXML
    ToggleButton runToggleBtn;
    private Stage templateStage;
    private void initIcons() {
        addFileBtn.setGraphic(new FontIcon());
        compileBtn.setGraphic(new FontIcon());
        templateBtn.setGraphic(new FontIcon());
        runToggleBtn.setGraphic(new FontIcon());
        searchIconBtn.setGraphic(new FontIcon());
        replaceIconBtn.setGraphic(new FontIcon());
        helpIconBtn.setGraphic(new FontIcon());

    }
    @FXML
    private void handleAddNewFile() {
        EditorController.getInstance().handleAddNewFile();
    }

    @FXML
    public CompletableFuture<Pair<EditorContext, Boolean>> handleCompile(){
        EditorController.getInstance()
                .saveContext();
        Editor editor = CPCompound.getEditor();
        if (editor.getCurrentContext() == null) return CompletableFuture.completedFuture(new Pair<>(null,false));
        var console = ConsoleController.getInstance();
        console.clear();
        console.logToUser("Compiling \""+editor.getCurrentContext().getPath()+"\" ...");
        return editor.compile(editor.getCurrentContext(), console.getOutputStream(), console.getErrorStream());
    }

    @FXML
    public void handleExecute(){
        Editor editor = CPCompound.getEditor();
        if (runToggleBtn.isSelected()) {
            runToggleBtn.setText("Stop");
            handleCompile().whenComplete((result, throwable) -> {
                if(!result.getValue()) {
                    Platform.runLater(()->{
                        runToggleBtn.setText("Run");
                        runToggleBtn.setSelected(false);
                    });
                    return;
                }
                EditorContext context = result.getKey();
                var console = ConsoleController.getInstance();
                console.clear();
                console.logToUser("Executing \""+context.getExePath()+"\" ...");
                console.openInput();
                editor.execute(context, console.getInputStream(), console.getOutputStream(),
                                console.getErrorStream(), false)
                        .whenComplete((_r,_t)->{
                            Platform.runLater(()->{
                                runToggleBtn.setText("Run");
                                runToggleBtn.setSelected(false);
                                console.closeInput();
                            });
                        });
            });
        }else{
            editor.stopExecute();
            runToggleBtn.setSelected(true);
            //TODO stop code
            CPCompound.getLogger().info("Stopping program");
        }

    }
    @FXML
    private void handleOpenTemplateStage() throws IOException {
        System.out.println("open template");
        if(templateStage !=null && templateStage.isShowing()) {
            templateStage.toFront();
        }else{
            templateStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(CPCompound.class.getResource("fxml/template-main.fxml"));
            templateStage.setScene(new Scene(fxmlLoader.load(),500,650));
            templateStage.setTitle("Code Template");
            templateStage.show();
        }
    }
    // for custom stage title bar's button function
    // @FXML
    // private void minimizeWindow() {
    // currentStage.setIconified(true);
    // }
    // @FXML
    // private void adjustWindow() {
    // if (currentStage.isMaximized()) {
    // currentStage.setMaximized(false);
    // } else {
    // currentStage.setMaximized(true);
    // }
    // }
    // @FXML
    // private void closeWindow() {
    // currentStage.close();
    // }
    @FXML
    public void initialize() {
        instance = this;
        initIcons();
        CPCompound.getLogger().info("initialize MainEditorToolBar");
    }

    public static ToolBarController getInstance() {
        return instance;
    }
}
