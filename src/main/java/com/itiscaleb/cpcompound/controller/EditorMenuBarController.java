package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.CPCompound;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.kordamp.ikonli.javafx.FontIcon;

public class EditorMenuBarController {
    @FXML
    Button homeBtn;
    @FXML
    Button fileBtn;
    @FXML
    Button checkerBtn;
    @FXML
    Button generatorBtn;
    @FXML
    Button noteSystemBtn;

    public Button getSettingBtn() {
        return settingBtn;
    }

    public Button getNoteSystemBtn() {
        return noteSystemBtn;
    }

    public Button getGeneratorBtn() {
        return generatorBtn;
    }

    public Button getCheckerBtn() {
        return checkerBtn;
    }

    public Button getFileBtn() {
        return fileBtn;
    }

    public Button getHomeBtn() {
        return homeBtn;
    }

    @FXML
    Button settingBtn;

    public Button getCurrentActiveMenuItem() {
        return currentActiveMenuItem;
    }

    public void setCurrentActiveMenuItem(Button currentActiveMenuItem) {
        this.currentActiveMenuItem = currentActiveMenuItem;
    }

    public Button currentActiveMenuItem=fileBtn;
    @FXML
    public void handleMenuSwitch(javafx.event.ActionEvent event){
        Button clickedButton = (Button)event.getSource();
        CPCompound.getBaseController().getEditorController().getEditorFunctionPaneController().assignFunctionTab(clickedButton.getId(),clickedButton);
    }
    private void initIcons() {
        homeBtn.setGraphic(new FontIcon());
        fileBtn.setGraphic(new FontIcon());
        checkerBtn.setGraphic(new FontIcon());
        generatorBtn.setGraphic(new FontIcon());
        noteSystemBtn.setGraphic(new FontIcon());
        settingBtn.setGraphic(new FontIcon());
    }
    @FXML
    public void initialize() {
        initIcons();
        currentActiveMenuItem = this.fileBtn;
        System.out.println("initialize MainEditorMenuBar");
    }
}
