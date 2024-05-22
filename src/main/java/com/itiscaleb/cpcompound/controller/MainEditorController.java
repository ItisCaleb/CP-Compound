package com.itiscaleb.cpcompound.controller;

import java.io.File;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.editor.Editor;
import com.itiscaleb.cpcompound.editor.EditorContext;
import com.itiscaleb.cpcompound.langServer.LSPProxy;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.eclipse.lsp4j.*;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.kordamp.ikonli.javafx.FontIcon;

public class MainEditorController {


    private final KeyCombination saveCombination =  new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN);
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
    @FXML
    Button functionTabButton, terminalTabButton;
    @FXML
    StackPane functionPaneContentArea;
    Button currentActiveMenuItem = new Button();
    //id,object
    final private Map<String,VBox> functionPaneCache = new HashMap<>();

    private VBox  currentFunctionContent;
    CodeArea mainTextArea = new CodeArea();
    Tab currentTab;
    List<Diagnostic> diagnostics;

    Popup diagPopup;
    Label diagPopupLabel;
    final TabManager tabManager = new TabManager();
    ContextMenu completionMenu;

    boolean switched = false;

    private void switchCodeArea(Tab setUpTab){
        switched = true;
        VirtualizedScrollPane<CodeArea> vsPane = new VirtualizedScrollPane<>(mainTextArea);
        setUpTab.setContent(vsPane);
        setUpTab.setOnClosed(event -> handleTabClosed(event, setUpTab));
        currentTab = setUpTab;

        // switch text area content
        mainTextArea.replaceText(0, mainTextArea.getText().length(),
                CPCompound.getEditor().getCurrentContext().getCode());
    }

    @FXML
    private void handleAddNewFile() {
        Editor editor = CPCompound.getEditor();
        String key = editor.addContext();
        newTab(key);
    }

    private void newTab(String key) {

        Tab newTab = new Tab(key);
        newTab.setUserData(key);

        CPCompound.getEditor().switchContext(key);
        switchCodeArea(newTab);

        tabManager.addTab(newTab, key.substring(key.lastIndexOf("/") + 1));
        editorTextTabPane.getTabs().add(newTab);
        editorTextTabPane.getSelectionModel().select(newTab);
    }

    @FXML
    private void handleTabClosed(Event event, Tab closedTab){
        if(!tabManager.getTabSaveState(closedTab)){
            saveContext(closedTab);
            CPCompound.getEditor().removeContext((String) closedTab.getUserData());
        }
    }
    private void setHandleChangeTab(){
        editorTextTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if(newTab != null){
                String key = (String)newTab.getUserData();
                CPCompound.getEditor().switchContext(key);
                switchCodeArea(newTab);
            }
        });
    }
    private void initEditorTextArea() {
        mainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if(switched){
                switched = false;
                return;
            }
            Editor editor = CPCompound.getEditor();
            EditorContext context = editor.getCurrentContext();
            context.setCode(newValue);
            LSPProxy proxy = CPCompound.getLSPProxy(context.getLang());
            // request to change
            proxy.didChange(context);

            // request for completion request
            int paragraph = mainTextArea.getCurrentParagraph();
            int column = mainTextArea.getCaretColumn();
            int index = mainTextArea.getCaretPosition() - 1;
            if (!newValue.isEmpty()
                    && newValue.charAt(index) != '\n'
                    && newValue.charAt(index) != ' '){
                proxy.requestCompletion(context, new Position(paragraph, column));
            } else {
                completionMenu.hide();
            }

            var spans = editor.computeHighlighting(context)
                    .overlay(mainTextArea.getStyleSpans(0, mainTextArea.getLength()),
                            (a,b)->{
                                var arr = new ArrayList<String>();
                                arr.addAll(a);
                                arr.addAll(b);
                                return arr;
                            });
            // highlight
            //mainTextArea.setStyleSpans(0, spans);
        });
        initEditorUtility();
        initDiagnosticTooltip();
        initDiagnosticRendering();
        initCompletionTooltip();
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
    private void assignFunctionTab(String itemId,Button sourceButton){
        currentActiveMenuItem.setStyle("-fx-background-color: transparent;");
        FontIcon itemIcon = (FontIcon) currentActiveMenuItem.getGraphic();
        itemIcon.setStyle(itemIcon.getStyle()+"-fx-icon-color: #CCCCCC;");
        currentActiveMenuItem = sourceButton;
        currentActiveMenuItem.setStyle("-fx-background-color: #4a4b4e;");
        itemIcon = (FontIcon) currentActiveMenuItem.getGraphic();
        itemIcon.setStyle(itemIcon.getStyle()+"-fx-icon-color: #FFFFFF;");
        currentActiveMenuItem.setGraphic(itemIcon);
        currentActiveMenuItem = sourceButton;
        switch(itemId){
            case "Home-button":
                loadContent("/com/itiscaleb/cpcompound/fxml/home.fxml");
                functionTabButton.setText("Home");
                break;
            case "File-button":
                loadContent("/com/itiscaleb/cpcompound/fxml/file-treeView.fxml");
                functionTabButton.setText("File View");
                break;
            case "Checker-button":
                loadContent("/com/itiscaleb/cpcompound/fxml/checker.fxml");
                functionTabButton.setText("Checker");
                break;
            case "Generator-button":
                loadContent("/com/itiscaleb/cpcompound/fxml/generator.fxml");
                functionTabButton.setText("Generator");
                break;
            case "Note-system-button":
                loadContent("/com/itiscaleb/cpcompound/fxml/note-system.fxml");
                functionTabButton.setText("Note System");
                break;
            case "Setting-button":
                loadContent("/com/itiscaleb/cpcompound/fxml/setting.fxml");
                functionTabButton.setText("Setting");
                break;
            default:
        }
    }
    @FXML
    private void handleMenuSwitch(javafx.event.ActionEvent event){
        Button clickedButton = (Button)event.getSource();
        assignFunctionTab(clickedButton.getId(),clickedButton);
    }
    @FXML
    private void handleTabSwitch(javafx.event.ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        if (clickedButton == functionTabButton) {
            functionTabButton.setStyle("-fx-border-color: transparent transparent transparent white;-fx-opacity: 1");
            terminalTabButton.setStyle("-fx-border-color: transparent transparent transparent transparent;-fx-opacity: 0.5");
            functionPaneContentArea.getChildren().setAll(currentFunctionContent);
        } else if (clickedButton == terminalTabButton) {
            terminalTabButton.setStyle("-fx-border-color: transparent transparent transparent white;-fx-opacity: 1");
            functionTabButton.setStyle("-fx-border-color: transparent transparent transparent transparent;-fx-opacity: 0.5");
            loadContent("/com/itiscaleb/cpcompound/fxml/terminal.fxml");
        }
    }

    private void loadContent(String fxmlFile) {
        try {
            VBox content = FXMLLoader.load(getClass().getResource(fxmlFile));
            content.prefWidthProperty().bind(functionPaneContentArea.widthProperty());
            content.prefHeightProperty().bind(functionPaneContentArea.heightProperty());
            if(!content.getId().equals("terminal")){
                if(functionPaneCache.containsKey(content.getId())){
                    functionPaneContentArea.getChildren().setAll(functionPaneCache.get(content.getId()));
                }else{
                    currentFunctionContent = content;
                    functionPaneContentArea.getChildren().setAll(content);
                    functionPaneCache.put(content.getId(),content);
                }
            }else{
                functionPaneContentArea.getChildren().setAll(content);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initFunctonPane(Button menuButton,String fxmlFilePath){
        loadContent(fxmlFilePath);
        currentActiveMenuItem = menuButton;
        assignFunctionTab(fxmlFilePath,menuButton);
    }
    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            initIcons();
            initEditorTextArea();
            setHandleChangeTab();
            initFunctonPane(fileBtn,"/com/itiscaleb/cpcompound/fxml/file-treeView.fxml");
        });
        System.out.println("initialize");
    }


    int rangeToPosition(StyleClassedTextArea area, Position p) {
        return area.getAbsolutePosition(p.getLine(), p.getCharacter());
    }

    private void initEditorUtility() {
        mainTextArea.setParagraphGraphicFactory(LineNumberFactory.get(mainTextArea));
        final Pattern whiteSpace = Pattern.compile("^\\s+");
        mainTextArea.addEventHandler(KeyEvent.KEY_PRESSED, KE -> {
            // auto-indent
            if (KE.getCode() == KeyCode.ENTER) {
                int caretPosition = mainTextArea.getCaretPosition();
                // allow shift-enter
                if(KE.isShiftDown()){
                    mainTextArea.insertText(caretPosition, "\n");
                }
                int currentParagraph = mainTextArea.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher(mainTextArea.getParagraph(currentParagraph - 1).getSegments().get(0));
                if (m0.find())
                    Platform.runLater(() -> mainTextArea.insertText(caretPosition, m0.group()));
            }

            // replace tab to four space
            if (KE.getCode() == KeyCode.TAB) {
                int caretPosition = mainTextArea.getCaretPosition();
                mainTextArea.replaceText(caretPosition - 1, caretPosition, "    ");
            }

            // auto complete bracket
            int caretPosition = mainTextArea.getCaretPosition();
            String right = "";
            switch (KE.getText()) {
                case "[" -> right = "]";
                case "(" -> right = ")";
                case "{" -> right = "}";
            }
            if(!right.isEmpty()){
                String finalRight = right;
                Platform.runLater(()->{
                    mainTextArea.insertText(caretPosition + 1, finalRight);
                });
            }
        });

        // hide completion menu when caret moved
        mainTextArea.caretPositionProperty().addListener((obs, oldPosition, newPosition) -> {
            if(newPosition < oldPosition){
                completionMenu.hide();
            }
        });

        // save code
        mainTextArea.setOnKeyPressed(event -> {
            if (saveCombination.match(event)) {
                saveContext(currentTab);
            }
        });
    }

    private void saveContext(Tab tab){
        EditorContext context = CPCompound.getEditor().getContext((String) tab.getUserData());
        if(context.isTemp()){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("C File", "*.c"),
                    new FileChooser.ExtensionFilter("C++ File", "*.cc", "*.cpp"),
                    new FileChooser.ExtensionFilter("Python File","*.py"),
                    new FileChooser.ExtensionFilter("All file", "*.*"));
            File file = fileChooser.showSaveDialog(editorTextTabPane.getScene().getWindow());

            // Reference:
            // https://microsoft.github.io/language-server-protocol/specifications/lsp/3.17/specification/#textDocument_didRename
            // to rename a file, we need to close it an open it
            if(file != null){
                tabManager.saveTab(tab);
                LSPProxy proxy = CPCompound.getLSPProxy(context.getLang());
                proxy.didClose(context);
                context.setPath(file);
                context.setTemp(false);
                context.save();
                proxy = CPCompound.getLSPProxy(context.getLang());
                proxy.didOpen(context);
            }
        }else{
            tabManager.saveTab(tab);
            context.save();
        }
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
        mainTextArea.setMouseOverTextDelay(Duration.ofMillis(500));
        mainTextArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {

            for (Diagnostic diagnostic : diagnostics) {
                System.out.println(diagnostic.getMessage());
                Range range = diagnostic.getRange();

                int from = rangeToPosition(mainTextArea, range.getStart());
                int to = rangeToPosition(mainTextArea, range.getEnd());
                int chIdx = e.getCharacterIndex();
                Point2D pos = e.getScreenPosition();
                if (chIdx >= from && chIdx <= to) {
                    diagPopupLabel.setText(diagnostic.getMessage());
                    diagPopup.show(mainTextArea, pos.getX(), pos.getY() + 10);
                    break;
                }
            }
        });
        mainTextArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> {
            diagPopup.hide();
        });
    }

    private void initCompletionTooltip() {
        completionMenu = new ContextMenu();
        completionMenu.setOnAction((event) -> {
            MenuItem item = (MenuItem) event.getTarget();
            String text = item.getText();
            Range range = (Range) item.getUserData();
            int from = rangeToPosition(mainTextArea, range.getStart());
            int to = rangeToPosition(mainTextArea, range.getEnd());
            mainTextArea.replaceText(from, to, text);
        });

        completionMenu.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.SPACE || (e.getCode() == KeyCode.ENTER && e.isShiftDown())) {
                e.consume();
                completionMenu.hide();
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

                Optional<Bounds> opt = mainTextArea.getCaretBounds();

                if (opt.isPresent()) {
                    double x = opt.get().getCenterX();
                    double y = opt.get().getCenterY();
                    completionMenu.show(mainTextArea, x + 10, y);
                }
            }
        });

        // listen for completion change
        CPCompound.getEditor().getCompletionList().addListener(listener);
    }

    private void initDiagnosticRendering() {
        // render diagnostic from language server
        ListChangeListener<? super Diagnostic> listener = (list) -> Platform.runLater(() -> {
            // do your GUI stuff here
            this.diagnostics = (List<Diagnostic>) list.getList();
            mainTextArea.setStyleSpans(0,
                    computeDiagnostic(
                            this.diagnostics,
                            mainTextArea.getLength()));
//                            .overlay(mainTextArea.getStyleSpans(0, mainTextArea.getLength()),
//                                    (a,b)->{
//                                        var arr = new ArrayList<String>();
//                                        arr.addAll(a);
//                                        arr.addAll(b);
//                                        return arr;
//                                    }));
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
            int from = rangeToPosition(mainTextArea, range.getStart());
            int to = rangeToPosition(mainTextArea, range.getEnd());
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
