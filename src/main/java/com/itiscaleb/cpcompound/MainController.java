package com.itiscaleb.cpcompound;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itiscaleb.cpcompound.editor.EditorContext;
import com.itiscaleb.cpcompound.utils.SysInfo;
import com.itiscaleb.cpcompound.utils.Utils;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class MainController {
    @FXML
    private Label welcomeText;

    @FXML
    private CodeArea editorTextArea;

    public void initialize(){
        CPCompound.mainController = this;
        initCodeArea();

    }

    private void initCodeArea(){
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
        editorTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            EditorContext context = CPCompound.getEditor().getCurrentContext();
            context.setCode(newValue);
            CPCompound.getLSPProxy().didChange(context);
        });

        // render diagnostic from language server
        EditorContext context = CPCompound.getEditor().getCurrentContext();
        ListChangeListener<? super Diagnostic> listener = (list)-> Platform.runLater(() -> {
            // do your GUI stuff here
            editorTextArea.setStyleSpans(0,
                    computeDiagnostic(
                            (List<Diagnostic>) list.getList(),
                            context.getCode().length()));
        });
        context.getDiagnostics().addListener(listener);
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }


    private Path downloadFromHTTP(String url) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Downloading \"" + url + "\"");
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET().build();
        File downloadDir = new File("./downloads");
        downloadDir.mkdir();
        HttpResponse<Path> res = client.send(req,
                HttpResponse.BodyHandlers
                        .ofFileDownload(downloadDir.getCanonicalFile().toPath(), CREATE, WRITE));
        CPCompound.getLogger().info(res.body());
        return res.body();
    }


    @FXML
    protected void download(){
        try{
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI("https://api.github.com/repos/clangd/clangd/releases/latest"))
                    .GET().build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            JsonObject json = new Gson().fromJson(res.body(), JsonObject.class);
            SysInfo.OS os = SysInfo.getOS();
            String arch = SysInfo.getArch();
            if(arch.equals("x86_64") || os == SysInfo.OS.WIN ||
                    (arch.equals("arm64") && os == SysInfo.OS.MAC)){
                String substr = "clangd-" + os.name;
                JsonArray arr = json.get("assets").getAsJsonArray();
                for (JsonElement elem : arr) {
                    if(elem.getAsJsonObject().get("name").getAsString().contains(substr)){
                        String url = elem.getAsJsonObject().get("browser_download_url").getAsString();
                        if(!CPCompound.getConfig().cpp_lang_server_path.isEmpty()) break;
                        Path path = downloadFromHTTP(url);
                        CPCompound.getConfig().cpp_lang_server_path = "./installed/"+ Utils.unzipFolder(path, "./installed");
                        CPCompound.getConfig().save();
                        CPCompound.getLogger().info(path);
                        break;
                    }
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }
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
                case Error -> {
                    spansBuilder.add(Collections.singleton("underlined-red"), to - from);
                }
                case Warning -> {
                    spansBuilder.add(Collections.singleton("underlined-yellow"), to-from);
                }
            }
        }
        spansBuilder.add(Collections.emptyList(), codeLength - last);
        return spansBuilder.create();
    }
}