package com.itiscaleb.cpcompound;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itiscaleb.cpcompound.editor.Editor;
import com.itiscaleb.cpcompound.editor.EditorContext;
import com.itiscaleb.cpcompound.misc.SysInfo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class MainController {
    @FXML
    private Label welcomeText;

    @FXML
    private CodeArea editorTextArea;

    public void initialize(){
        MainApplication.mainController = this;
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
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onEditorInput(){
        EditorContext context = Editor.getInstance().getCurrentContext();
        System.out.println(context.getCode());
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
        System.out.println(res.body());
        return res.body();
    }
    public static void unzipFolder(Path source, Path target) throws IOException {

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {

            // list files in zip
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {

                boolean isDirectory = zipEntry.getName().endsWith(File.separator);

                if (isDirectory) {
                    Files.createDirectories(target);
                } else {
                    
                    if (target.getParent() != null) {
                        if (Files.notExists(target.getParent())) {
                            Files.createDirectories(target.getParent());
                        }
                    }

                    // copy files, nio
                    Files.copy(zis, target, StandardCopyOption.REPLACE_EXISTING);
                }

                zipEntry = zis.getNextEntry();

            }
            zis.closeEntry();

        }

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
                        Path path = downloadFromHTTP(url);
                        System.out.println(path);
                        break;
                    }
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}