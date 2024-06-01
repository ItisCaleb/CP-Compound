package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.CPCompound;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.kordamp.ikonli.carbonicons.CarbonIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

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
    private void handleCompile(){
        CPCompound.getBaseController().getEditorController().doCompile();
    }
    @FXML
    private void handleExecute(){
        if (runToggleBtn.isSelected()) {
            runToggleBtn.setText("Stop");
            CPCompound.getBaseController().getEditorController().doExecute();
        }else{
            runToggleBtn.setText("Run");
            //TODO stop code
            System.out.println("stop");
        }
    }
    @FXML
    private void handleOpenTemplateStage() throws IOException {
        System.out.println("open template");
        Stage templateStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(CPCompound.class.getResource("fxml/template-main.fxml"));
        templateStage.setScene(new Scene(fxmlLoader.load(),600,800));
        templateStage.show();
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
        System.out.println("initialize MainEditorToolBar");
    }
}
