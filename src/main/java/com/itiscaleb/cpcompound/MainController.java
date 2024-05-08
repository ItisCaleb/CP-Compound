package com.itiscaleb.cpcompound;

import com.itiscaleb.cpcompound.editor.EditorContext;
import com.itiscaleb.cpcompound.langServer.LSPProxy;
import com.itiscaleb.cpcompound.utils.ClangdDownloader;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;
import org.eclipse.lsp4j.*;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController {

    @FXML
    private CodeArea editorTextArea;

    private List<Diagnostic> diagnostics;

    private Popup diagPopup;
    private Label diagPopupLabel;

    private Popup completionPopup;
    private Label completionPopupLabel;

    public void initialize(){
        CPCompound.mainController = this;
        initCodeArea();

    }

    private void initCodeArea(){
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
            System.out.println("123 ");
            var compList = (List<CompletionItem>) list.getList();
            if(compList.isEmpty()){
                completionPopup.hide();
            }else {
                StringBuilder builder = new StringBuilder();
                for (CompletionItem item : compList) {
                    builder.append(item.getLabel());
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

        // listen for diagnostics change
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


    @FXML
    protected void downloadClangd(){
        new ClangdDownloader().download();
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

