package com.itiscaleb.cpcompound.controller;

import java.io.File;
import java.nio.file.Path;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.component.EditorArea;
import com.itiscaleb.cpcompound.editor.Editor;
import com.itiscaleb.cpcompound.editor.EditorContext;
import com.itiscaleb.cpcompound.langServer.LSPProxy;
import com.itiscaleb.cpcompound.utils.TabManager;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

public class EditorController {
    @FXML
    private TabPane  editorTextTabPane;

    static EditorController instance;

    Tab currentTab;

    final TabManager tabManager = new TabManager();

    @FXML
    public void initialize() {
        instance = this;
        Platform.runLater(()->{
            setHandleChangeTab();
            CPCompound.getLogger().info("initialize editor");
        });
    }

    public static EditorController getInstance() {
        return instance;
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
        EditorArea area = new EditorArea(CPCompound.getEditor().getCurrentContext());
        VirtualizedScrollPane<CodeArea> vsPane =
                new VirtualizedScrollPane<>(area);
        newTab.setContent(vsPane);
        newTab.setOnClosed(event -> handleTabClosed(event, newTab));
        area.getContext().addOnChanged((e, _o, newValue)->{
            if(newValue) tabManager.changeTab(newTab);
            else tabManager.saveTab(newTab);
        });
        currentTab = newTab;

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
    }
    private void setHandleChangeTab(){
        editorTextTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if(newTab != null){
                String key = (String)newTab.getUserData();
                CPCompound.getEditor().switchContext(key);
            }
        });
    }

    public boolean saveContext(){
        return this.saveContext(currentTab);
    }

    public boolean saveContext(Tab tab){
        if(tab == null) return false;
        EditorContext context = CPCompound.getEditor().getContext((String) tab.getUserData());
        boolean result = saveContext(context);
        if(result) tabManager.saveTab(tab);
        return result;
    }

    public boolean saveContext(EditorContext context){
        if(context == null) return false;
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
                LSPProxy proxy = CPCompound.getLSPProxy(context.getLang());
                proxy.didClose(context);
                context.setPath(file);
                context.setTemp(false);
                context.save();
                proxy = CPCompound.getLSPProxy(context.getLang());
                proxy.didOpen(context);
                return true;
            }
            return false;
        }else{
            context.save();
            return true;
        }
    }



}
