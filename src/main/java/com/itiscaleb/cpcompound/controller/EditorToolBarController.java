package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.editor.Editor;
import com.itiscaleb.cpcompound.editor.EditorContext;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.util.Pair;
import org.kordamp.ikonli.carbonicons.CarbonIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.concurrent.CompletableFuture;

public class EditorToolBarController {
//    @FXML
//    ToolBar mainEditorToolBar;
    @FXML
    Button addFileBtn,compileBtn, templateBtn, searchIconBtn, replaceIconBtn, helpIconBtn;//, minimizeWindowBtn, closeWindowBtn;
//    @FXML
//    ToggleButton adjustWindowBtn;
    @FXML
    ToggleButton runToggleBtn;

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
        CPCompound.getBaseController().getEditorController().handleAddNewFile();
    }

    @FXML
    public CompletableFuture<Pair<EditorContext, Boolean>> handleCompile(){
        CPCompound.getBaseController()
                .getEditorController()
                .saveContext();
        Editor editor = CPCompound.getEditor();
        if (editor.getCurrentContext() == null) return CompletableFuture.completedFuture(new Pair<>(null,false));
        return editor.compile(editor.getCurrentContext(), System.out, System.err);
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
                editor.execute(context, System.in, System.out, System.err, false)
                        .whenComplete((_r,_t)->{
                            Platform.runLater(()->{
                                runToggleBtn.setText("Run");
                                runToggleBtn.setSelected(false);
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
        initIcons();
        CPCompound.getLogger().info("initialize MainEditorToolBar");
    }
}
