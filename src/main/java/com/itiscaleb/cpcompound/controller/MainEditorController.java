package com.itiscaleb.cpcompound.controller;

import java.net.URL;
import java.util.ResourceBundle;

<<<<<<< Updated upstream
=======
import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.editor.EditorContext;
import com.itiscaleb.cpcompound.langServer.LSPProxy;
import com.itiscaleb.cpcompound.utils.FileManager;
>>>>>>> Stashed changes
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.github.palexdev.materialfx.controls.MFXTooltip;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
//import org.fxmisc.flowless.VirtualizedScrollPane;
import javafx.util.Duration;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.kordamp.ikonli.javafx.FontIcon;
import org.w3c.dom.ls.LSOutput;


public class MainEditorController {
    public MainEditorController() {}
    public MainEditorController(Stage stage) {
        setCurrentStage(stage);
    }
    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }
    @FXML
    private Stage currentStage;
    @FXML
    private TabPane funtionTabPane,editorTextTabPane;
    @FXML
    private SplitPane codeAreaSplitPane,codeAreaBase;
    @FXML
    private CodeArea editorTextArea1;
    @FXML
    private AnchorPane editorTabPaneBase;
    @FXML
    Tab tab1;
    @FXML
    ToolBar mainEditorToolBar;
    @FXML
    MFXButton compileBtn,templateBtn,runBtn,searchIconBtn,replaceIconBtn,helpIconBtn,minimizeWindowBtn,closeWindowBtn;
    @FXML
    ToggleButton adjustWindowBtn;
    @FXML
    Button homeBtn,fileBtn,checkerBtn,generatorBtn,noteSystemBtn,settingBtn;
<<<<<<< Updated upstream
    private void initEditorTextArea() {
        editorTextArea1.setParagraphGraphicFactory(LineNumberFactory.get(editorTextArea1));
        VirtualizedScrollPane<CodeArea> vsPane = new VirtualizedScrollPane<>(editorTextArea1);
        tab1.setContent(vsPane);
=======

    @FXML
    CodeArea editorTextArea;

    List<Diagnostic> diagnostics;

    Popup diagPopup;
    Label diagPopupLabel;

    Popup completionPopup;
    Label completionPopupLabel;
    private void loadText2EditorTextArea(String fileName){
//        System.out.println("Current working directory: " + System.getProperty("user.dir"));

        String content = FileManager.readTextFile("../src/main/resources/com/itiscaleb/cpcompound/data/"+fileName);
        this.editorTextArea.append(content,"-fx-fill:red");
    }
    private void initEditorTextArea() {
        editorTextArea.setParagraphGraphicFactory(LineNumberFactory.get(editorTextArea));
        loadText2EditorTextArea("a.txt");
        VirtualizedScrollPane<CodeArea> vsPane = new VirtualizedScrollPane<>(editorTextArea);
        tab1.setContent(vsPane);
        editorTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            EditorContext context = CPCompound.getEditor().getCurrentContext();
            context.setCode(newValue);
            LSPProxy proxy = CPCompound.getLSPProxy(context.getLang());
            // request to change
            proxy.didChange(context);

            // request for completion request
            proxy.requestCompletion(context, new Position(editorTextArea.getCurrentParagraph(), editorTextArea.getCaretColumn()));
        });
//        initEditorUtility();
//        initDiagnosticTooltip();
//        initDiagnosticRendering();
//        initCompletionTooltip();
>>>>>>> Stashed changes
    }
    private void initIcons(){
        compileBtn.setGraphic(new FontIcon());
        templateBtn.setGraphic(new FontIcon());
        runBtn.setGraphic(new FontIcon());
        searchIconBtn.setGraphic(new FontIcon());
        replaceIconBtn.setGraphic(new FontIcon());
        helpIconBtn.setGraphic(new FontIcon());
        homeBtn.setGraphic(new FontIcon());
        fileBtn.setGraphic(new FontIcon());
        checkerBtn.setGraphic(new FontIcon());
        generatorBtn.setGraphic(new FontIcon());
        noteSystemBtn.setGraphic(new FontIcon());
        settingBtn.setGraphic(new FontIcon());
//        for custom stage title bar's button
//        minimizeWindowBtn.setGraphic(new FontIcon());
//        adjustWindowBtn.setGraphic(new FontIcon());
//        closeWindowBtn.setGraphic(new FontIcon());
    }
// for custom stage title bar's button function
//    @FXML
//    private void minimizeWindow() {
//        currentStage.setIconified(true);
//    }
//    @FXML
//    private void adjustWindow() {
//        if (currentStage.isMaximized()) {
//            currentStage.setMaximized(false);
//        } else {
//            currentStage.setMaximized(true);
//        }
//    }
//    @FXML
//    private void closeWindow() {
//        currentStage.close();
//    }
    @FXML
    void initialize() {
        Platform.runLater(() -> {
            initIcons();
            initEditorTextArea();
        });
        System.out.println("initialize");
    }
}
