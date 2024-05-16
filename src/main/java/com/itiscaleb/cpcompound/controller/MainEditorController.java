package com.itiscaleb.cpcompound.controller;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.editor.EditorContext;
import com.itiscaleb.cpcompound.utils.FileManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.kordamp.ikonli.javafx.FontIcon;

public class MainEditorController {
    public MainEditorController() {
    }

    public MainEditorController(Stage stage) {
        setCurrentStage(stage);
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }
    private final KeyCombination saveCombination = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
    @FXML
    private Stage currentStage;
    @FXML
    private TabPane funtionTabPane, editorTextTabPane;
    @FXML
    private SplitPane codeAreaSplitPane, codeAreaBase;
    @FXML
    private AnchorPane editorTabPaneBase;

    @FXML
    ToolBar mainEditorToolBar;
    @FXML
    MFXButton addFileBtn,compileBtn, templateBtn, runBtn, searchIconBtn, replaceIconBtn, helpIconBtn, minimizeWindowBtn,
            closeWindowBtn;
    @FXML
    ToggleButton adjustWindowBtn;
    @FXML
    Button homeBtn, fileBtn, checkerBtn, generatorBtn, noteSystemBtn, settingBtn;
    @FXML
    CodeArea editorTextArea;
    @FXML
    Tab currentTab;
    List<Diagnostic> diagnostics;

    Popup diagPopup;
    Label diagPopupLabel;

    ContextMenu completionMenu;
    private Map<Object, String> tabContentMap = new HashMap<>();
    private Map<Object, Boolean> tabContentSaveStateMap = new HashMap<>();
    private void loadText2EditorTextArea(String fileName,Tab tab) {
        // System.out.println("Current working directory: " +System.getProperty("user.dir"));
        String content = FileManager.readTextFile("../src/main/resources/com/itiscaleb/cpcompound/data/" + fileName);
        editorTextArea.replace(0,editorTextArea.getLength(),content, "-fx-fill:red");
        tabContentMap.put(tab, content);
    }
    private void saveTextFile(String fileName) {
        FileManager.writeTextFile("../src/main/resources/com/itiscaleb/cpcompound/data/" + fileName,"");
    }
    static private int currentTabCount = 1;
    private void setCodeAreaListener(CodeArea codeArea) {
        codeArea.setOnKeyPressed(event -> {
            if (saveCombination.match(event)) {
                String currentText = codeArea.getText();
                tabContentMap.put(currentTab, currentText);
                String fileName = currentTab.getText().substring(0,currentTab.getText().length()-1);
                currentTab.setText(fileName);
                FileManager.writeTextFile(fileName,currentText);
                System.out.println("Content saved for tab: " + currentTab.getText());
            }
        });
    }
    private void setUpCodeArea(Tab setUpTab){
        System.out.println("set up : "+setUpTab.getText());
        editorTextArea.setParagraphGraphicFactory(LineNumberFactory.get(editorTextArea));
        VirtualizedScrollPane<CodeArea> vsPane = new VirtualizedScrollPane<>(editorTextArea);
        setUpTab.setContent(vsPane);
        setCodeAreaListener(editorTextArea);
        setUpTab.setOnClosed(event -> handleTabClosed(event, setUpTab));
    }
    @FXML
    private void handleAddNewFile() {
        currentTabCount++;
        String fileName="Untitled"+currentTabCount+".txt";
        Tab newTab = new Tab(fileName+"*");
        saveTextFile(fileName);
        tabContentMap.put(newTab,"");
        tabContentSaveStateMap.put(newTab,false);
        editorTextArea.replace(0,editorTextArea.getLength(),"","-fx-fill:white");
        setUpCodeArea(newTab);
        editorTextTabPane.getTabs().add(newTab);
        editorTextTabPane.getSelectionModel().select(newTab);
    }
    @FXML
    private void handleTabClosed(Event event, Tab closedTab){
        System.out.println("Tab" + currentTabCount+"closed");
        if(!tabContentSaveStateMap.get(closedTab)){
            tabContentMap.remove(closedTab);
        }

    }
    private void setHandleChangeTab(){
        editorTextTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            System.out.println("change tab "+newTab.getText());
            //change tab
            currentTab = newTab;
            System.out.println("current tab "+currentTab.getText());
            loadContentIntoCodeArea();
            setUpCodeArea(newTab);
        });
    }
    private void loadContentIntoCodeArea() {
        System.out.println("load content:");
        System.out.println(tabContentMap.get(currentTab));
        editorTextArea.replace(0,editorTextArea.getLength(),tabContentMap.get(currentTab),"-fx-fill:white");
        System.out.println("editor content:"+editorTextArea.getText());
    }
    private void initEditorTextArea() {
//        Tab initTab = new Tab(currentTab.getText());
//        tabContentMap.put(initTab,"");
//        loadText2EditorTextArea(initTab.getText(),initTab);
//        editorTextArea.setParagraphGraphicFactory(LineNumberFactory.get(editorTextArea));
//        VirtualizedScrollPane<CodeArea> vsPane = new VirtualizedScrollPane<>(editorTextArea);
//        currentTab.setContent(vsPane);
//        System.out.println("init: "+tabContentMap.get(initTab));
//        tabContentSaveStateMap.put(initTab,true);
//        setUpCodeArea(initTab);

//        editorTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
//            EditorContext context = CPCompound.getEditor().getCurrentContext();
//            context.setCode(newValue);
//            LSPProxy proxy = CPCompound.getLSPProxy(context.getLang());
//            // request to change
//            proxy.didChange(context);
//
//            // request for completion request
//            int paragraph = editorTextArea.getCurrentParagraph();
//            int column = editorTextArea.getCaretColumn();
//            String text = editorTextArea.getText();
//            proxy.requestCompletion(context, new Position(paragraph, column));
//        });
//        initEditorUtility();
//        initDiagnosticTooltip();
//        initDiagnosticRendering();
//        initCompletionTooltip();
    }

    private void initIcons() {
        addFileBtn.setGraphic(new FontIcon());
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
        // for custom stage title bar's button
        // minimizeWindowBtn.setGraphic(new FontIcon());
        // adjustWindowBtn.setGraphic(new FontIcon());
        // closeWindowBtn.setGraphic(new FontIcon());
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
        Platform.runLater(() -> {
            initIcons();
            initEditorTextArea();
            setHandleChangeTab();
        });
        System.out.println("initialize");
    }

    public void reloadContext() {
        Platform.runLater(() -> {
            var contexts = CPCompound.getEditor().getContexts();
            editorTextTabPane.getTabs().clear();
            for (var key : contexts.keySet()) {
                var tab = new Tab();
                tab.setText(key.substring(key.lastIndexOf("/")));
                editorTextTabPane.getTabs().add(tab);
                tab.setUserData(key);
            }
        });
    }

    int rangeToPosition(StyleClassedTextArea area, Position p) {
        return area.getAbsolutePosition(p.getLine(), p.getCharacter());
    }

    private void initEditorUtility() {
        editorTextArea.setParagraphGraphicFactory(LineNumberFactory.get(editorTextArea));
        final Pattern whiteSpace = Pattern.compile("^\\s+");
        editorTextArea.addEventHandler(KeyEvent.KEY_PRESSED, KE -> {
            // auto-indent
            if (KE.getCode() == KeyCode.ENTER && !KE.isShiftDown()) {
                int caretPosition = editorTextArea.getCaretPosition();
                int currentParagraph = editorTextArea.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher(editorTextArea.getParagraph(currentParagraph - 1).getSegments().get(0));
                if (m0.find())
                    Platform.runLater(() -> editorTextArea.insertText(caretPosition, m0.group()));
            }
            // replace tab to four space
            if (KE.getCode() == KeyCode.TAB) {
                int caretPosition = editorTextArea.getCaretPosition();
                editorTextArea.replaceText(caretPosition - 1, caretPosition, "    ");
            }
        });
    }

    private void initDiagnosticTooltip() {
        // tooltip
        diagPopup = new Popup();
        diagPopupLabel = new Label();
        diagPopup.getContent().add(diagPopupLabel);
        diagPopupLabel.setStyle(
                "-fx-background-color: black;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 5;");
        editorTextArea.setMouseOverTextDelay(Duration.ofMillis(500));
        editorTextArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
            for (Diagnostic diagnostic : diagnostics) {
                Range range = diagnostic.getRange();
                int from = rangeToPosition(editorTextArea, range.getStart());
                int to = rangeToPosition(editorTextArea, range.getEnd());
                int chIdx = e.getCharacterIndex();
                Point2D pos = e.getScreenPosition();
                if (chIdx >= from && chIdx <= to) {
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

    private void initCompletionTooltip() {
        completionMenu = new ContextMenu();
        completionMenu.setOnAction((event) -> {
            MenuItem item = (MenuItem) event.getTarget();
            String text = item.getText();
            Range range = (Range) item.getUserData();
            int from = rangeToPosition(editorTextArea, range.getStart());
            int to = rangeToPosition(editorTextArea, range.getEnd());
            editorTextArea.replaceText(from, to, text);
        });

        completionMenu.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.SPACE) {
                e.consume();
            }
        });
        completionMenu.getStyleClass().add("completion-menu");

        ListChangeListener<? super CompletionItem> listener = (list) -> Platform.runLater(() -> {
            // do your GUI stuff here
            var compList = (List<CompletionItem>) list.getList();
            if (compList.isEmpty()) {
                completionMenu.hide();
            } else {
                List<MenuItem> tmpList = new ArrayList<>();
                int count = 0;
                for (CompletionItem item : compList) {
                    if (count++ > 30)
                        break;
                    var edit = item.getTextEdit().getLeft();
                    var menuItem = new MenuItem(edit.getNewText());
                    menuItem.setUserData(edit.getRange());
                    menuItem.getStyleClass().add("completion-menu-item");
                    tmpList.add(menuItem);
                }
                completionMenu.getItems().setAll(tmpList);

                Optional<Bounds> opt = editorTextArea.getCaretBounds();

                if (opt.isPresent()) {
                    double x = opt.get().getCenterX();
                    double y = opt.get().getCenterY();
                    completionMenu.show(editorTextArea, x + 10, y);
                }
            }
        });

        // listen for completion change
        CPCompound.getEditor().getCompletionList().addListener(listener);
    }

    private void initDiagnosticRendering() {
        // render diagnostic from language server
        EditorContext context = CPCompound.getEditor().getCurrentContext();
        ListChangeListener<? super Diagnostic> listener = (list) -> Platform.runLater(() -> {
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

    // Reference:
    // https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/SpellCheckingDemo.java
    // for compute diagnostic style
    public StyleSpans<Collection<String>> computeDiagnostic(List<Diagnostic> diagnostics, int codeLength) {
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
