package com.itiscaleb.cpcompound.controller;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.component.CompletionMenu;
import com.itiscaleb.cpcompound.component.EditorPopup;
import com.itiscaleb.cpcompound.component.EditorStyler;
import com.itiscaleb.cpcompound.editor.Editor;
import com.itiscaleb.cpcompound.editor.EditorContext;
import com.itiscaleb.cpcompound.editor.SemanticHighlighter;
import com.itiscaleb.cpcompound.langServer.LSPProxy;
import com.itiscaleb.cpcompound.utils.TabManager;
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
import javafx.stage.FileChooser;
import org.eclipse.lsp4j.*;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.richtext.model.TwoDimensional;

public class EditorController {
    private final KeyCombination saveCombination =  new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN);
    @FXML
    private TabPane  editorTextTabPane;

    static EditorController instance;

    CodeArea mainTextArea = new CodeArea();
    Tab currentTab;
    List<Diagnostic> diagnostics;

    EditorPopup diagPopup;
    final TabManager tabManager = new TabManager();
    ContextMenu completionMenu;
    EditorContext lastContext = null;

    StyleSpans<Collection<String>> diagnosticSpans = null;
    StyleSpans<Collection<String>> highlightSpans = null;
    StyleSpans<Collection<String>> semanticSpans = null;

    boolean stopNextCompletion = false;

    @FXML
    public void initialize() {
        instance = this;
        Platform.runLater(()->{
            initEditorTextArea();
            setHandleChangeTab();

            CPCompound.getLogger().info("initialize editor");
        });
    }

    public static EditorController getInstance() {
        return instance;
    }

    private void switchCodeArea(Tab setUpTab){
        VirtualizedScrollPane<CodeArea> vsPane = new VirtualizedScrollPane<>(mainTextArea);
        setUpTab.setContent(vsPane);
        setUpTab.setOnClosed(event -> handleTabClosed(event, setUpTab));
        currentTab = setUpTab;

        // switch text area content
        mainTextArea.replaceText(0, mainTextArea.getText().length(),
                CPCompound.getEditor().getCurrentContext().getCode());
    }



    @FXML
    public void handleAddNewFile() {
        try {
            Editor editor = CPCompound.getEditor();
            String key = editor.addContext();
            newTab(key);
        }catch (Exception e){
            CPCompound.getLogger().error("Error occurred", e);
        }
    }

    public void addNewFile(File file){
        try {
            Editor editor = CPCompound.getEditor();
            String key = editor.addContext(Path.of(file.getCanonicalPath()),false);
            for (Tab tab: editorTextTabPane.getTabs()){
                if(tab.getUserData().equals(key)){
                    editorTextTabPane.getSelectionModel().select(tab);
                    return;
                }
            }
            newTab(key);
        }catch (Exception e){
            CPCompound.getLogger().error("Error occurred", e);
        }
    }

    private void newTab(String key) {

        Tab newTab = new Tab(key);
        newTab.setUserData(key);
        CPCompound.getEditor().switchContext(key);
        switchCodeArea(newTab);
        if(FunctionPaneController.getInstance().getCheckerController()!=null){
            FunctionPaneController.getInstance().getCheckerController().updatePath();
        }

        if(editorTextTabPane.getTabs().isEmpty()){
            lastContext = CPCompound.getEditor().getContext(key);
        }
        tabManager.addTab(newTab, key.substring(key.lastIndexOf("/") + 1));
        editorTextTabPane.getTabs().add(newTab);
        editorTextTabPane.getSelectionModel().select(newTab);


    }

    @FXML
    private void handleTabClosed(Event event, Tab closedTab){
        if(!tabManager.getTabSaveState(closedTab)){
            //saveContext(closedTab);
        }

        CPCompound.getEditor().removeContext((String) closedTab.getUserData());
        if(CPCompound.getEditor().getCurrentContext() == null){
            Platform.runLater(mainTextArea::clear);
        }

    }
    private void setHandleChangeTab(){
        editorTextTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if(newTab != null){
                String key = (String)newTab.getUserData();
                CPCompound.getEditor().switchContext(key);
                if(FunctionPaneController.getInstance().getCheckerController()!=null){
                    FunctionPaneController.getInstance().getCheckerController().updatePath();
                }
                switchCodeArea(newTab);
            }
        });
    }
    String ignoreChars = "!@#$%^&*()+{}[]:;'\">/?~` \t\n";

    private void initEditorTextArea() {
        mainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            Editor editor = CPCompound.getEditor();
            EditorContext context = editor.getCurrentContext();
            if(context == null) return;
            if(lastContext != context){
                lastContext = context;
                this.diagnostics = context.getDiagnostics();
                requestHighlight();
                return;
            }
            context.setCode(newValue);
            LSPProxy proxy = CPCompound.getLSPProxy(context.getLang());
            // request to change
            proxy.didChange(context);

            // request for completion request
            int paragraph = mainTextArea.getCurrentParagraph();
            int column = mainTextArea.getCaretColumn();
            int index = mainTextArea.getCaretPosition() - 1;
            if (!stopNextCompletion && index >= 0){
                char c = newValue.charAt(index);
                if(ignoreChars.indexOf(c) == -1){
                    proxy.requestCompletion(context, new Position(paragraph, column));
                }
            } else {
                stopNextCompletion = false;
                completionMenu.hide();
            }
            tabManager.changeTab(currentTab);
            requestHighlight();
        });
        initEditorUtility();
        initDiagnosticTooltip();
        initDiagnosticRendering();
        initCompletionTooltip();
    }

    private void requestHighlight(){
        Editor editor = CPCompound.getEditor();
        EditorContext context = editor.getCurrentContext();
        LSPProxy proxy = CPCompound.getLSPProxy(context.getLang());
        this.diagnosticSpans = computeDiagnostic(mainTextArea, this.diagnostics);

        this.semanticSpans = SemanticHighlighter.computeHighlighting(mainTextArea, proxy.semanticTokens(context));
        // highlight
        this.highlightSpans = editor.computeHighlighting(context);
        refreshSpans();
    }

    private void refreshSpans(){
        EditorStyler.setSpans(mainTextArea, highlightSpans, diagnosticSpans, semanticSpans);
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
                if(KE.isShiftDown()){
                    // we need to prepend \n ourself
                    int caretPosition = mainTextArea.getAnchor();
                    int currentParagraph = mainTextArea.getCurrentParagraph();
                    Matcher m0 = whiteSpace.matcher(mainTextArea.getParagraph(currentParagraph).getSegments().get(0));
                    if(m0.find()){
                        Platform.runLater(() -> mainTextArea.insertText(caretPosition, "\n"+m0.group()));
                    }else {
                        mainTextArea.insertText(caretPosition, "\n");
                    }
                }else {
                    // the editor will prepend \n for us first
                    int caretPosition = mainTextArea.getAnchor();
                    // allow shift-enter

                    int currentParagraph = mainTextArea.getCurrentParagraph();
                    Matcher m0 = whiteSpace.matcher(mainTextArea.getParagraph(currentParagraph - 1).getSegments().get(0));
                    if (m0.find()) {
                        Platform.runLater(() -> mainTextArea.insertText(caretPosition, m0.group()));
                    }
                }


            }

            // replace tab to four space
            if (KE.getCode() == KeyCode.TAB) {
                int caretPosition = mainTextArea.getCaretPosition();
                mainTextArea.replaceText(caretPosition - 1, caretPosition, "    ");
            }

//            // auto complete bracket
//            int caretPosition = mainTextArea.getAnchor() - 1;
//            String right = "";
//            switch (KE.getText()) {
//                case "[" -> right = "]";
//                case "(" -> right = ")";
//            }
//            if(!right.isEmpty()){
//                String finalRight = right;
//                Platform.runLater(()->{
//                    mainTextArea.insertText(caretPosition + 1, finalRight);
//                });
//            }
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

    public void saveContext(){
        this.saveContext(currentTab);
    }

    public void saveContext(Tab tab){
        if(tab == null) return;
        EditorContext context = CPCompound.getEditor().getContext((String) tab.getUserData());
        if(context == null) return;
        if(context.isTemp()){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            fileChooser.setInitialFileName(context.getFileName());
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("C++ File", "*.cc", "*.cpp"),
                    new FileChooser.ExtensionFilter("C File", "*.c"),
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
                requestHighlight();
            }
        }else{
            tabManager.saveTab(tab);
            context.save();
        }
    }

    private void initDiagnosticTooltip() {
        // tooltip
        diagPopup = new EditorPopup();

        mainTextArea.setMouseOverTextDelay(Duration.ofMillis(500));
        mainTextArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
            EditorContext context = CPCompound.getEditor().getCurrentContext();
            LSPProxy proxy = CPCompound.getLSPProxy(context.getLang());
            Point2D screenPos = e.getScreenPosition();
            int chIdx = e.getCharacterIndex();

            var pos = mainTextArea.offsetToPosition(chIdx, TwoDimensional.Bias.Forward);
            String hover = proxy.hover(context, new Position(pos.getMajor(), pos.getMinor()));
            StringBuilder builder = new StringBuilder();

            if(hover != null){
                builder.append(hover);
            }
            if(diagnostics != null) {
                for (Diagnostic diagnostic : diagnostics) {
                    Range range = diagnostic.getRange();

                    int from = rangeToPosition(mainTextArea, range.getStart());
                    int to = rangeToPosition(mainTextArea, range.getEnd());
                    if (chIdx >= from && chIdx <= to) {
                        if (!builder.isEmpty()) builder.append("\n-----\n");
                        builder.append(diagnostic.getMessage());
                    }
                }
            }
            if(!builder.isEmpty()){
                diagPopup.setText(builder.toString());
                diagPopup.show(mainTextArea, screenPos.getX(), screenPos.getY() + 10);
            }

        });
        mainTextArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> diagPopup.hide());
    }

    private void initCompletionTooltip() {
        completionMenu = new CompletionMenu();
        completionMenu.setOnAction((event) -> {
            stopNextCompletion = true;
            MenuItem item = (MenuItem) event.getTarget();
            String text = item.getText();
            Range range = (Range) item.getUserData();
            int from = rangeToPosition(mainTextArea, range.getStart());
            int to = rangeToPosition(mainTextArea, range.getEnd());
            mainTextArea.replaceText(from, to, text);
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
            this.diagnosticSpans = computeDiagnostic(mainTextArea, this.diagnostics);
            refreshSpans();
        });

        // listen for diagnostics change
        CPCompound.getEditor().getDiagnostics().addListener(listener);
    }

    // Reference:
    // https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/SpellCheckingDemo.java
    // for compute diagnostic style
    public StyleSpans<Collection<String>> computeDiagnostic(StyleClassedTextArea area, List<Diagnostic> diagnostics) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        int last = 0;
        if(diagnostics == null) return null;
        for (Diagnostic diagnostic : diagnostics) {
            // convert line and character to index
            Range range = diagnostic.getRange();
            int from = rangeToPosition(area, range.getStart());
            int to = rangeToPosition(area, range.getEnd());
            int len = from - last;
            if(len < 0) continue;
            spansBuilder.add(Collections.emptyList(), len);
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
        spansBuilder.add(Collections.emptyList(), area.getLength() - last);
        return spansBuilder.create();
    }

}
