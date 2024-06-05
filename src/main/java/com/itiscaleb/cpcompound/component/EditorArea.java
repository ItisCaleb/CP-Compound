package com.itiscaleb.cpcompound.component;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.controller.EditorController;
import com.itiscaleb.cpcompound.editor.Editor;
import com.itiscaleb.cpcompound.editor.EditorContext;
import com.itiscaleb.cpcompound.editor.SemanticHighlighter;
import com.itiscaleb.cpcompound.langServer.LSPProxy;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.richtext.model.TwoDimensional;

import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditorArea extends CodeArea {
    private static final KeyCombination saveCombination =  new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN);
    private static final String ignoreChars = "!@#$%^&*()+{}[]:;'\">/?~` \t\n";
    private final EditorContext context;

    private ContextMenu completionMenu;
    EditorPopup diagPopup;

    private boolean stopNextCompletion;

    StyleSpans<Collection<String>> diagnosticSpans = null;
    StyleSpans<Collection<String>> highlightSpans = null;
    StyleSpans<Collection<String>> semanticSpans = null;
    
    public EditorArea(EditorContext context) {
        super();
        this.context = context;
        this.insertText(0, context.getCode());
        initEditorTextArea();
        requestHighlight();
    }

    public EditorContext getContext() {
        return context;
    }

    private void initEditorTextArea() {
        initObserver();
        initUtility();
        initDiagnosticTooltip();
        initCompletionTooltip();
    }

    void initObserver(){
        this.textProperty().addListener((observable, oldValue, newValue) -> {
            if(context == null) return;
            context.setCode(newValue);
            LSPProxy proxy = CPCompound.getLSPProxy(context.getLang());
            // request to change
            proxy.didChange(context);

            // request for completion request
            int paragraph = this.getCurrentParagraph();
            int column = this.getCaretColumn();
            int index = this.getCaretPosition() - 1;
            if (!stopNextCompletion && index >= 0){
                char c = newValue.charAt(index);
                if(ignoreChars.indexOf(c) == -1){
                    proxy.requestCompletion(context, new org.eclipse.lsp4j.Position(paragraph, column));
                }
            } else {
                stopNextCompletion = false;
                completionMenu.hide();
            }
            requestHighlight();
        });
    }
    
    void initUtility(){
        this.setParagraphGraphicFactory(LineNumberFactory.get(this));
        final Pattern whiteSpace = Pattern.compile("^\\s+");
        this.addEventHandler(KeyEvent.KEY_PRESSED, KE -> {
            // save code
            if (saveCombination.match(KE)) {
                Path lastPath = context.getPath();
                boolean result = EditorController.getInstance().saveContext(context);
                if(result && context.getPath() != lastPath){
                    requestHighlight();
                }
            }

            // auto-indent
            if (KE.getCode() == KeyCode.ENTER) {
                if(KE.isShiftDown()){
                    // we need to prepend \n ourself
                    int caretPosition = this.getAnchor();
                    int currentParagraph = this.getCurrentParagraph();
                    Matcher m0 = whiteSpace.matcher(this.getParagraph(currentParagraph).getSegments().get(0));
                    if(m0.find()){
                        Platform.runLater(() -> this.insertText(caretPosition, "\n"+m0.group()));
                    }else {
                        this.insertText(caretPosition, "\n");
                    }
                }else {
                    // the editor will prepend \n for us first
                    int caretPosition = this.getAnchor();
                    // allow shift-enter

                    int currentParagraph = this.getCurrentParagraph();
                    Matcher m0 = whiteSpace.matcher(this.getParagraph(currentParagraph - 1).getSegments().get(0));
                    if (m0.find()) {
                        Platform.runLater(() -> this.insertText(caretPosition, m0.group()));
                    }
                }
            }

            // replace tab to four space
            if (KE.getCode() == KeyCode.TAB) {
                int caretPosition = this.getCaretPosition();
                this.replaceText(caretPosition - 1, caretPosition, "    ");
            }

//            // auto complete bracket
//            int caretPosition = this.getAnchor() - 1;
//            String right = "";
//            switch (KE.getText()) {
//                case "[" -> right = "]";
//                case "(" -> right = ")";
//            }
//            if(!right.isEmpty()){
//                String finalRight = right;
//                Platform.runLater(()->{
//                    this.insertText(caretPosition + 1, finalRight);
//                });
//            }
        });

        // hide completion menu when caret moved
        this.caretPositionProperty().addListener((obs, oldPosition, newPosition) -> {
            if(newPosition < oldPosition){
                completionMenu.hide();
            }
        });
    }

    private void requestHighlight(){
        Editor editor = CPCompound.getEditor();
        EditorContext context = editor.getCurrentContext();
        LSPProxy proxy = CPCompound.getLSPProxy(context.getLang());
        this.diagnosticSpans = computeDiagnostic(this, context.getDiagnostics());

        this.semanticSpans = SemanticHighlighter.computeHighlighting(this, proxy.semanticTokens(context));
        // highlight
        this.highlightSpans = editor.computeHighlighting(context);
        refreshSpans();
    }

    private void refreshSpans(){
        EditorStyler.setSpans(this, highlightSpans, diagnosticSpans, semanticSpans);
    }

    int rangeToPosition(StyleClassedTextArea area, org.eclipse.lsp4j.Position p) {
        return area.getAbsolutePosition(p.getLine(), p.getCharacter());
    }


    private void initDiagnosticTooltip() {
        // tooltip
        diagPopup = new EditorPopup();

        this.setMouseOverTextDelay(Duration.ofMillis(500));
        this.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
            EditorContext context = CPCompound.getEditor().getCurrentContext();
            LSPProxy proxy = CPCompound.getLSPProxy(context.getLang());
            Point2D screenPos = e.getScreenPosition();
            int chIdx = e.getCharacterIndex();

            var pos = this.offsetToPosition(chIdx, TwoDimensional.Bias.Forward);
            String hover = proxy.hover(context, new org.eclipse.lsp4j.Position(pos.getMajor(), pos.getMinor()));
            StringBuilder builder = new StringBuilder();

            if(hover != null){
                builder.append(hover);
            }
            List<Diagnostic> diagnostics = context.getDiagnostics();
            if(diagnostics != null) {
                for (Diagnostic diagnostic : diagnostics) {
                    Range range = diagnostic.getRange();

                    int from = rangeToPosition(this, range.getStart());
                    int to = rangeToPosition(this, range.getEnd());
                    if (chIdx >= from && chIdx <= to) {
                        if (!builder.isEmpty()) builder.append("\n-----\n");
                        builder.append(diagnostic.getMessage());
                    }
                }
            }
            if(!builder.isEmpty()){
                diagPopup.setText(builder.toString());
                diagPopup.show(this, screenPos.getX(), screenPos.getY() + 10);
            }

        });
        this.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> diagPopup.hide());
    }

    private void initCompletionTooltip() {
        completionMenu = new CompletionMenu();
        completionMenu.setOnAction((event) -> {
            stopNextCompletion = true;
            MenuItem item = (MenuItem) event.getTarget();
            String text = item.getText();
            Range range = (Range) item.getUserData();
            int from = rangeToPosition(this, range.getStart());
            int to = rangeToPosition(this, range.getEnd());
            this.replaceText(from, to, text);
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

                Optional<Bounds> opt = this.getCaretBounds();

                if (opt.isPresent()) {
                    double x = opt.get().getCenterX();
                    double y = opt.get().getCenterY();
                    completionMenu.show(this, x + 10, y);
                }
            }
        });

        // listen for completion change
        context.getCompletionList().addListener(listener);
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
