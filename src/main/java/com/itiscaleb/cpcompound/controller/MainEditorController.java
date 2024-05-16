package com.itiscaleb.cpcompound.controller;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.editor.EditorContext;
import com.itiscaleb.cpcompound.utils.FileManager;
import com.itiscaleb.cpcompound.utils.TabManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
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
    private TabPane functionTabPane, editorTextTabPane;
    @FXML
    private SplitPane codeAreaSplitPane, codeAreaBase;
    @FXML
    private AnchorPane editorTabPaneBase;
    @FXML
    ToolBar mainEditorToolBar;
    @FXML
    MFXButton addFileBtn,compileBtn, templateBtn, runBtn, searchIconBtn, replaceIconBtn, helpIconBtn, minimizeWindowBtn, closeWindowBtn;
    @FXML
    ToggleButton adjustWindowBtn;
    @FXML
    Button homeBtn, fileBtn, checkerBtn, generatorBtn, noteSystemBtn, settingBtn;

    CodeArea currentTextArea = new CodeArea();
    Tab currentTab;
    List<Diagnostic> diagnostics;

    Popup diagPopup;
    Label diagPopupLabel;
    final TabManager tabManager = new TabManager();
    ContextMenu completionMenu;

    private void loadText2EditorTextArea(String fileName,Tab tab) {
        // System.out.println("Current working directory: " +System.getProperty("user.dir"));
        String content = FileManager.readTextFile("../src/main/resources/com/itiscaleb/cpcompound/data/" + fileName);
        replaceCodeArea(currentTextArea, content);
        tabManager.updateTabContent(tab,content);
    }
    private void saveTextFile(String fileName,String content) {
        FileManager.writeTextFile("../src/main/resources/com/itiscaleb/cpcompound/data/" + fileName,content);
    }
    static private int currentTabCount = 1;
    private void setCodeAreaListener(CodeArea codeArea) {
        codeArea.setOnKeyPressed(event -> {
            if (saveCombination.match(event)) {
                String currentText = codeArea.getText();
                System.out.println("save content:\n " + currentText);
                System.out.println("in "+ currentTab.getText()+" tab");
                tabManager.updateTabContent(currentTab, currentText);
                tabManager.saveTab(currentTab, currentText);
                saveTextFile(currentTab.getText(),currentText);
            }
        });
    }
    private void setUpCodeArea(Tab setUpTab){
        currentTextArea.setParagraphGraphicFactory(LineNumberFactory.get(currentTextArea));
        VirtualizedScrollPane<CodeArea> vsPane = new VirtualizedScrollPane<>(currentTextArea);
        setUpTab.setContent(vsPane);
        setCodeAreaListener(currentTextArea);
        setUpTab.setOnClosed(event -> handleTabClosed(event, setUpTab));
        currentTab = setUpTab;
    }
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/itiscaleb/cpcompound/fxml/dynamic-codeArea-template.fxml"));
    @FXML
    private void handleAddNewFile() {
        currentTabCount++;
        String fileName="Untitled"+currentTabCount+".txt";
        Tab newTab = new Tab();
        try {
            newTab = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tabManager.addTab(newTab,fileName,"");
        replaceCodeArea(currentTextArea,"");
        setUpCodeArea(newTab);
        editorTextTabPane.getTabs().add(newTab);
        editorTextTabPane.getSelectionModel().select(newTab);
    }
    @FXML
    private void handleTabClosed(Event event, Tab closedTab){
        System.out.println("Tab" + currentTabCount+"closed");
        if(!tabManager.getTabSaveState(closedTab)){
            tabManager.removeTab(closedTab);
        }
    }
    private void replaceCodeArea(CodeArea codeArea, String content){
        codeArea.replace(0,codeArea.getLength(),content,"-fx-fill:white");
    }
    private void setHandleChangeTab(){
        editorTextTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            replaceCodeArea(currentTextArea,tabManager.getTabContent(newTab));
            setUpCodeArea(newTab);
        });
    }
    private void initEditorTextArea() {
//        currentTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
//            EditorContext context = CPCompound.getEditor().getCurrentContext();
//            context.setCode(newValue);
//            LSPProxy proxy = CPCompound.getLSPProxy(context.getLang());
//            // request to change
//            proxy.didChange(context);
//
//            // request for completion request
//            int paragraph = currentTextArea.getCurrentParagraph();
//            int column = currentTextArea.getCaretColumn();
//            String text = currentTextArea.getText();
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
    private boolean isDragging = false;
    private void updateTabWidths() {
        double tabPaneWidth = functionTabPane.getWidth();
        double newTabWidth = tabPaneWidth / 2;  // 保证有两个标签，每个宽度为一半
        Label tabLabel;
        String tabLabelStyle =
                "-fx-font-weight: bold;" +
                "-fx-alignment: CENTER;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 0 0 0 0;";
        // 设置每个标签的宽度
        String tabStyle=
                "-fx-pref-height: 38px;" +
                "-fx-border-radius: 5px 5px 0 0;" +
                "-fx-background-radius: 5px 5px 0 0;" +
                "-fx-border-width: 1px;" +
                "-fx-padding: 0 0 0 0;"+
                "-fx-pref-width: " + newTabWidth + ";";
        for (Tab tab : functionTabPane.getTabs()) {
            tab.setStyle(tabStyle);
            tabLabel = new Label(tab.getText());
            tabLabelStyle +="-fx-pref-width: " + newTabWidth + ";";
            tabLabel.setStyle(tabLabelStyle);
            tab.setGraphic(tabLabel);

        }
//        for (Node node : functionTabPane.lookupAll("*")) {
//            System.out.println(node.getClass().getSimpleName() + ": " + node.getStyleClass());
//        }

    }
    private boolean isEventFromTabHeader(ScrollEvent event) {
        // 递归检查事件源是否位于TabPane的标签头部
        Node target = (Node) event.getTarget();
        while (target != null && target != codeAreaSplitPane) {
            if (target.getStyleClass().contains("tab-header-area")) {
                return true;
            }
            target = target.getParent();
        }
        return false;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            initIcons();
            initEditorTextArea();
            setHandleChangeTab();

            // 监听分隔符位置变化
            codeAreaSplitPane.getDividers().get(0).positionProperty().addListener((obs, oldPos, newPos) -> {
                isDragging = true;
            });

            // 监听鼠标事件来更新Tab宽度
            codeAreaSplitPane.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
                if (isDragging) {
                    updateTabWidths();
                    isDragging = false;
                }
            });
            codeAreaSplitPane.addEventFilter(ScrollEvent.SCROLL, event -> {
                if (isEventFromTabHeader(event)) {
                    event.consume(); // 消耗事件，防止TabPane滚动
                }
            });

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
        currentTextArea.setParagraphGraphicFactory(LineNumberFactory.get(currentTextArea));
        final Pattern whiteSpace = Pattern.compile("^\\s+");
        currentTextArea.addEventHandler(KeyEvent.KEY_PRESSED, KE -> {
            // auto-indent
            if (KE.getCode() == KeyCode.ENTER && !KE.isShiftDown()) {
                int caretPosition = currentTextArea.getCaretPosition();
                int currentParagraph = currentTextArea.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher(currentTextArea.getParagraph(currentParagraph - 1).getSegments().get(0));
                if (m0.find())
                    Platform.runLater(() -> currentTextArea.insertText(caretPosition, m0.group()));
            }
            // replace tab to four space
            if (KE.getCode() == KeyCode.TAB) {
                int caretPosition = currentTextArea.getCaretPosition();
                currentTextArea.replaceText(caretPosition - 1, caretPosition, "    ");
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
        currentTextArea.setMouseOverTextDelay(Duration.ofMillis(500));
        currentTextArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
            for (Diagnostic diagnostic : diagnostics) {
                Range range = diagnostic.getRange();
                int from = rangeToPosition(currentTextArea, range.getStart());
                int to = rangeToPosition(currentTextArea, range.getEnd());
                int chIdx = e.getCharacterIndex();
                Point2D pos = e.getScreenPosition();
                if (chIdx >= from && chIdx <= to) {
                    diagPopupLabel.setText(diagnostic.getMessage());
                    diagPopup.show(currentTextArea, pos.getX(), pos.getY() + 10);
                    break;
                }
            }
        });
        currentTextArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> {
            diagPopup.hide();
        });
    }

    private void initCompletionTooltip() {
        completionMenu = new ContextMenu();
        completionMenu.setOnAction((event) -> {
            MenuItem item = (MenuItem) event.getTarget();
            String text = item.getText();
            Range range = (Range) item.getUserData();
            int from = rangeToPosition(currentTextArea, range.getStart());
            int to = rangeToPosition(currentTextArea, range.getEnd());
            currentTextArea.replaceText(from, to, text);
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

                Optional<Bounds> opt = currentTextArea.getCaretBounds();

                if (opt.isPresent()) {
                    double x = opt.get().getCenterX();
                    double y = opt.get().getCenterY();
                    completionMenu.show(currentTextArea, x + 10, y);
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
            currentTextArea.setStyleSpans(0,
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
            int from = currentTextArea
                    .getAbsolutePosition(
                            range.getStart().getLine(),
                            range.getStart().getCharacter());
            int to = currentTextArea
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
