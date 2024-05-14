package com.itiscaleb.cpcompound.controller;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.editor.EditorContext;
import com.itiscaleb.cpcompound.langServer.LSPProxy;
import com.itiscaleb.cpcompound.utils.ClangdDownloader;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.kordamp.ikonli.javafx.FontIcon;


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

    @FXML
    CodeArea editorTextArea;

    List<Diagnostic> diagnostics;

    Popup diagPopup;
    Label diagPopupLabel;

    Popup completionPopup;
    Label completionPopupLabel;

    private void initEditorTextArea() {
        editorTextArea.setParagraphGraphicFactory(LineNumberFactory.get(editorTextArea));
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
        initEditorUtility();
        initDiagnosticTooltip();
        initDiagnosticRendering();
        initCompletionTooltip();
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
    public void initialize() {
        Platform.runLater(() -> {
            initIcons();
            initEditorTextArea();
        });
        System.out.println("initialize");
    }

    public void reloadContext(){
        Platform.runLater(() -> {
            var contexts = CPCompound.getEditor().getContexts();
            editorTextTabPane.getTabs().clear();
            for(var key: contexts.keySet()){
                var tab = new Tab();
                tab.setText(key.substring(key.lastIndexOf("/")));
                editorTextTabPane.getTabs().add(tab);
                tab.setUserData(key);
            }
        });
    }


    private void initEditorUtility(){
        editorTextArea.setParagraphGraphicFactory(LineNumberFactory.get(editorTextArea));
        final Pattern whiteSpace = Pattern.compile( "^\\s+" );
        editorTextArea.addEventHandler( KeyEvent.KEY_PRESSED, KE -> {
            // auto-indent
            if ( KE.getCode() == KeyCode.ENTER ) {
                int caretPosition = editorTextArea.getCaretPosition();
                int currentParagraph = editorTextArea.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher( editorTextArea.getParagraph( currentParagraph-1 ).getSegments().get( 0 ) );
                if ( m0.find() ) Platform.runLater( () -> editorTextArea.insertText( caretPosition, m0.group() ) );
            }
            // replace tab to four space
            if(KE.getCode() == KeyCode.TAB){
                int caretPosition = editorTextArea.getCaretPosition();
                editorTextArea.replaceText( caretPosition-1,caretPosition, "    ");
            }
        });
    }

    private void initDiagnosticTooltip(){
        // tooltip
        diagPopup = new Popup();
        diagPopupLabel = new Label();
        diagPopup.getContent().add(diagPopupLabel);
        diagPopupLabel.setStyle(
                "-fx-background-color: black;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 5;");
        editorTextArea.setMouseOverTextDelay(Duration.ofMillis(500));
        editorTextArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e->{
            for (Diagnostic diagnostic : diagnostics) {
                Range range = diagnostic.getRange();
                int from = editorTextArea
                        .getAbsolutePosition(
                                range.getStart().getLine(),
                                range.getStart().getCharacter());
                int to = editorTextArea
                        .getAbsolutePosition(
                                range.getEnd().getLine(),
                                range.getEnd().getCharacter());
                int chIdx = e.getCharacterIndex();
                Point2D pos = e.getScreenPosition();
                if(chIdx >= from && chIdx <= to){
                    diagPopupLabel.setText(diagnostic.getMessage());
                    diagPopup.show(editorTextArea, pos.getX(), pos.getY() + 10);
                    break;
                }
            }
        });
        editorTextArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> {
            diagPopup.hide();
        });
    }

    private void initCompletionTooltip(){
        completionPopup = new Popup();
        completionPopupLabel = new Label();
        completionPopup.getContent().add(completionPopupLabel);
        completionPopupLabel.setStyle(
                "-fx-background-color: black;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 5;");

        ListChangeListener<? super CompletionItem> listener = (list)-> Platform.runLater(() -> {
            // do your GUI stuff here
            var compList = (List<CompletionItem>) list.getList();
            if(compList.isEmpty()){
                completionPopup.hide();
            }else {
                StringBuilder builder = new StringBuilder();
                for (CompletionItem item : compList) {
                    builder.append(item.getLabel()).append("\n");
                }
                Optional<Bounds> opt = editorTextArea.getCaretBounds();
                if(opt.isPresent()){
                    completionPopupLabel.setText(builder.toString());
                    double x = opt.get().getCenterX();
                    double y = opt.get().getCenterY();
                    completionPopup.show(editorTextArea, x+10, y);
                }
            }
        });

        // listen for completion change
        CPCompound.getEditor().getCompletionList().addListener(listener);
    }

    private void initDiagnosticRendering(){
        // render diagnostic from language server
        EditorContext context = CPCompound.getEditor().getCurrentContext();
        ListChangeListener<? super Diagnostic> listener = (list)-> Platform.runLater(() -> {
            // do your GUI stuff here
            this.diagnostics = (List<Diagnostic>) list.getList();
            editorTextArea.setStyleSpans(0,
                    computeDiagnostic(
                            this.diagnostics,
                            context.getCode().length()));
        });

        // listen for diagnostics change
        CPCompound.getEditor().getDiagnostics().addListener(listener);
    }


    // Reference: https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/SpellCheckingDemo.java
    // for compute diagnostic style
    public StyleSpans<Collection<String>> computeDiagnostic(List<Diagnostic> diagnostics, int codeLength){
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        int last = 0;
        for (Diagnostic diagnostic : diagnostics) {
            // convert line and character to index
            Range range = diagnostic.getRange();
            int from = editorTextArea
                    .getAbsolutePosition(
                            range.getStart().getLine(),
                            range.getStart().getCharacter());
            int to = editorTextArea
                    .getAbsolutePosition(
                            range.getEnd().getLine(),
                            range.getEnd().getCharacter());
            spansBuilder.add(Collections.emptyList(), from - last);
            last = to;
            switch (diagnostic.getSeverity()) {
                case Error:
                    spansBuilder.add(Collections.singleton("underlined-red"), to - from);
                    break;
                case Warning:
                case Information:
                    spansBuilder.add(Collections.singleton("underlined-yellow"), to - from);
                    break;
            }
        }
        spansBuilder.add(Collections.emptyList(), codeLength - last);
        return spansBuilder.create();
    }

}
