package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.CPCompound;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.kordamp.ikonli.javafx.FontIcon;

public class MenuBarController {

    static MenuBarController instance;
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
        FunctionPaneController.getInstance().assignFunctionTab(clickedButton.getId(),clickedButton);
    }
    private void initIcons() {
        fileBtn.setGraphic(new FontIcon());
        checkerBtn.setGraphic(new FontIcon());
        generatorBtn.setGraphic(new FontIcon());
        noteSystemBtn.setGraphic(new FontIcon());
        settingBtn.setGraphic(new FontIcon());
    }
    @FXML
    public void initialize() {
        instance = this;
        initIcons();
        currentActiveMenuItem = this.fileBtn;
        CPCompound.getLogger().info("initialize MainEditorMenuBar");
    }

    public static MenuBarController getInstance() {
        return instance;
    }
}
