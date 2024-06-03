package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.CPCompound;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ConsoleController {
    private static ConsoleController instance;

    @FXML
    private StyleClassedTextArea inputArea, outputArea;

    @FXML
    private SplitPane splitPane;

    private final Queue<String> inputQueue = new LinkedList<>();
    int lastLine = 0;

    private InputStream inputStream;
    private OutputStream outputStream;
    private OutputStream errorStream;

    @FXML
    void initialize(){
        instance = this;
        initInputArea();
        inputStream = new InputStream() {
            String currentLine;
            int i = 0;
            @Override
            public int read() {
                if(currentLine == null || i >= currentLine.length()){
                    currentLine = inputQueue.poll();
                    if(currentLine == null) return -1;
                    if(currentLine.isEmpty()){
                        currentLine = inputQueue.poll();
                        return -1;
                    }
                    i = 0;
                }
                return currentLine.charAt(i++);
            }
        };
        outputStream = new OutputStream() {
            byte[] buffer = new byte[8192];
            int count = 0;
            @Override
            public void write(int b) throws IOException {
                buffer[count++] = (byte) b;
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                buffer = new byte[8192];
                count = 0;
                super.write(b, off, len);
                try {
                    String s = new String(buffer, StandardCharsets.UTF_8);
                    logToUser(s);
                }catch (Exception e){
                    CPCompound.getLogger().error("Error output", e);
                }
            }
        };
        errorStream = new OutputStream() {
            byte[] buffer = new byte[8192];
            int count = 0;
            @Override
            public void write(int b) throws IOException {
                buffer[count++] = (byte) b;
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                buffer = new byte[8192];
                count = 0;
                super.write(b, off, len);
                try {
                    String s = new String(buffer, StandardCharsets.UTF_8);
                    errorToUser(s);
                }catch (Exception e){
                    CPCompound.getLogger().error("Error output", e);
                }
            }
        };
    }


    private void initInputArea(){
        inputArea.addEventFilter(KeyEvent.KEY_PRESSED, (e)->{
            int maxPos = inputArea.getAbsolutePosition(lastLine, 0);
            if(inputArea.getSelection().getStart() <= maxPos){
                if(e.isControlDown()){
                    if(e.getCode() == KeyCode.V){
                        inputArea.getCaretSelectionBind().moveTo(lastLine,0);
                        inputArea.getCaretSelectionBind().moveToParEnd();
                    }
                    return;
                }
                if(e.getCode() == KeyCode.BACK_SPACE || e.getCode() == KeyCode.DELETE){
                    if(inputArea.getSelection().getLength() > 0){
                        inputArea.getCaretSelectionBind().updateStartTo(lastLine,0);
                    }
                }
                e.consume();
                inputArea.getCaretSelectionBind().moveTo(lastLine,0);
                inputArea.getCaretSelectionBind().moveToParEnd();
            }

        });

        inputArea.addEventHandler(KeyEvent.KEY_PRESSED,(e)->{
            int currentLine = inputArea.getCurrentParagraph();

            if(!e.isControlDown() && currentLine < lastLine){
                inputArea.getCaretSelectionBind().moveTo(lastLine,0);
                inputArea.getCaretSelectionBind().moveToParEnd();
            }

            if(e.getCode() == KeyCode.ENTER){
                if(e.isShiftDown()) return;

                for (int i=lastLine;i<currentLine;i++){
                    String s = inputArea.getText(i);
                    inputQueue.add(s+"\n");
                }
                lastLine = currentLine;
                inputArea.getUndoManager().forgetHistory();
            }
        });

    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public OutputStream getErrorStream() {
        return errorStream;
    }

    public void logToUser(String message){
        Platform.runLater(()->{
            outputArea.appendText(message+"\n");
        });
    }

    public void errorToUser(String message){
        Platform.runLater(()->{
            outputArea.append(message+"\n","console-error");
        });
    }

    public void clear(){
        clearInput();
        clearOutput();
    }

    public void clearInput(){
        Platform.runLater(()->{
            inputQueue.clear();
            lastLine = 0;
            inputArea.clear();
        });

    }

    public void clearOutput(){
        Platform.runLater(outputArea::clear);
    }

    public static ConsoleController getInstance() {
        return instance;
    }
}
