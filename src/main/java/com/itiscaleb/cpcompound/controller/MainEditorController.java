package com.itiscaleb.cpcompound.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
//import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;


public class MainEditorController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    @FXML
    private AnchorPane functionPane;
    @FXML
    private SplitPane codeAreaSplitPane;
    @FXML
    void mouseClickTest(MouseEvent event) {
        System.out.println("mouseClickTest");
    }
    @FXML
    void dragTest(MouseEvent event) {
        System.out.println("drag test");
    }
    @FXML
    void mousePressedTest(MouseEvent event) {
        System.out.println("mousePressed");
    }
    @FXML
    private void onKeyReleased() {
        updateLineNumbers();
    }
    @FXML
    private TextArea codeAreaTextArea;
    @FXML
    private ListView codeAreaLineViewer;
    private void updateLineNumbers() {
        String text = codeAreaTextArea.getText();
        String[] lines = text.split("\n", -1); // -1 to include empty lines at the end
        codeAreaLineViewer.getItems().clear();
        for (int i = 1; i <= lines.length; i++) {
            codeAreaLineViewer.getItems().add(String.valueOf(i));
        }
    }

    @FXML
    private CodeArea editorTextArea;
    @FXML
    private AnchorPane editorTabPaneBase;
    @FXML
    private VirtualizedScrollPane<CodeArea> vsPane;

    private void initEditorTextArea() {
        codeAreaSplitPane.getDividers().get(0).positionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                editorTabPaneBase.setPrefWidth(codeAreaSplitPane.getWidth());
                editorTabPaneBase.setPrefHeight(codeAreaSplitPane.getHeight());
//                System.out.println("editorTabPaneBase.getPrefWidth():"+editorTabPaneBase.getPrefWidth());
                vsPane.setPrefWidth(editorTabPaneBase.getPrefWidth());
//                System.out.println("vsPane.getPrefWidth():"+vsPane.getPrefWidth());
                vsPane.setPrefHeight(editorTabPaneBase.getPrefHeight());
                // 这里可以根据newValue来执行你需要的逻辑
            }
        });
        editorTextArea.setParagraphGraphicFactory(LineNumberFactory.get(editorTextArea));

//        editorTextArea.setPrefWidth(editorTabPaneBase.getWidth());
//        editorTextArea.setPrefHeight(editorTabPaneBase.getHeight());
        vsPane = new VirtualizedScrollPane<>(editorTextArea);
//        System.out.println(editorTabPaneBase.getPrefWidth());
        vsPane.setPrefWidth(editorTabPaneBase.getWidth());
        vsPane.setPrefHeight(editorTabPaneBase.getHeight());

        editorTabPaneBase.getChildren().add(vsPane);
        // 包装codeArea进VirtualizedScrollPane
//        VirtualizedScrollPane<CodeArea> scrollPane = new VirtualizedScrollPane<>(codeArea);
    }


    final private double lineNumberViewWidth = 40;
    @FXML
    void initialize() {
        //設定line number view的固定寬度
        Platform.runLater(() -> {
            initEditorTextArea();
            double position = lineNumberViewWidth / codeAreaSplitPane.getWidth();
//            System.out.println(codeAreaSplitPane.getWidth());
            codeAreaSplitPane.getDividers().get(0).positionProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldPosition, Number newPosition) {
                    double currentWidth = codeAreaSplitPane.getWidth();
                    if (currentWidth > lineNumberViewWidth) {
                        double expectedPosition = lineNumberViewWidth / currentWidth;
//                        codeAreaSplitPane.setDividerPositions(lineNumberViewWidth / currentWidth);
//                        if (Math.abs(newPosition.doubleValue() - expectedPosition) > 0.01) {
                            codeAreaSplitPane.setDividerPositions(expectedPosition);
//                                codeAreaSplitPane.setDividerPosition(0,position);
//                        }
                    }
                }
            });
//            codeAreaSplitPane.getDividers().get(0).positionProperty().addListener((observable,oldValue,newValue) -> {
//                codeAreaSplitPane.setDividerPosition(0, position);
//            });
//            codeAreaSplitPane.widthProperty().addListener(new ChangeListener<Number>() {
//                @Override
//                public void changed(ObservableValue<? extends Number> observable, Number oldWidth, Number newWidth) {
//                    if (newWidth.doubleValue() > lineNumberViewWidth) {
//                        codeAreaSplitPane.setDividerPositions(lineNumberViewWidth / newWidth.doubleValue());
//                    }
//                }
//            });
//            codeAreaSplitPane.setDividerPosition(0,position);



//            codeAreaLineViewer.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
//                @Override
//                public ListCell<String> call(ListView<String> listView) {
//                    return new ListCell<String>() {
//                        @Override
//                        protected void updateItem(String item, boolean empty) {
//                            super.updateItem(item, empty);
//                            if (!empty && item != null) {
//                                setText(item);
//                                System.out.println("codeAreaTextArea.getFont().getSize():"+codeAreaTextArea.getFont().getSize());
////                                codeAreaTextArea.getText().lines().
//                                setPrefHeight(codeAreaTextArea.getFont().getSize()); // 调整为适当的行高
//                            } else {
//                                setText(null);
//                            }
//                        }
//                    };
//                }
//            });

            // 获取TextArea的垂直滚动条并监听其滚动位置变化
//            ScrollBar textAreaVScrollBar = (ScrollBar) codeAreaTextArea.lookup(".scroll-bar:vertical");
//            if (textAreaVScrollBar != null) {
//                textAreaVScrollBar.valueProperty().addListener((obs, oldValue, newValue) -> {
//                    ScrollBar listViewVScrollBar = (ScrollBar) codeAreaLineViewer.lookup(".scroll-bar:vertical");
//                    if (listViewVScrollBar != null) {
//                        listViewVScrollBar.setValue(newValue.doubleValue());
//                    }
//                });
//            }

        });

        System.out.println("initialize");
        assert codeAreaSplitPane != null : "fx:id=\"codeAreaSplitPane\" was not injected: check your FXML file 'editor-main.fxml'.";

    }

}
