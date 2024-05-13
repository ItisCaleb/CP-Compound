////package com.itiscaleb.cpcompound.controller;
////import javafx.fxml.FXML;
////import javafx.scene.control.ListView;
////import javafx.scene.control.TextArea;
////public class TestController {
////    @FXML
////    private TextArea textArea;
////
////    @FXML
////    private ListView<String> lineNumbers;
////
////    @FXML
////    private void onTextChange() {
////        updateLineNumbers();
////    }
////
////    private void updateLineNumbers() {
////        String text = textArea.getText();
////        String[] lines = text.split("\n", -1); // -1 to include empty lines at the end
////        lineNumbers.getItems().clear();
////        for (int i = 1; i <= lines.length; i++) {
////            lineNumbers.getItems().add(String.valueOf(i));
////        }
////    }
////}
//package com.itiscaleb.cpcompound.controller;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.Tab;
//import javafx.scene.control.TabPane;
//import javafx.scene.control.TextArea;
//import javafx.scene.layout.HBox;
//import javafx.scene.text.Text;
//public class TestController {
//
//    @FXML
//    private TabPane tabPane;
//
//    public void initialize() {
//        // 初始設置
//    }
//
//    // 添加一個帶有關閉按鈕的新標籤
//    public void addNewTab(String title) {
//        Tab tab = new Tab();
//        TextArea textArea = new TextArea();
//
//        // 自定義標籤頭部
//        HBox tabHeader = new HBox();
//        Label titleLabel = new Label(title);
//        Button closeButton = new Button("X");
//        closeButton.setOnAction(event -> tabPane.getTabs().remove(tab));
//        tabHeader.getChildren().addAll(titleLabel, closeButton);
//
//        tab.setGraphic(tabHeader);
//        tab.setContent(textArea);
//        tabPane.getTabs().add(tab);
//    }
//}
package com.itiscaleb.cpcompound.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

public class TestController {
    @FXML
    private TabPane tabPane;
    @FXML
    private Pane scrollBar;

    @FXML
    public void initialize() {
        tabPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            double delta = event.getDeltaY();
            double newWidth = scrollBar.getPrefWidth() - delta;
            if (newWidth > tabPane.getWidth()) {
                newWidth = tabPane.getWidth();
            } else if (newWidth < 50) { // 最小寬度
                newWidth = 50;
            }
            scrollBar.setPrefWidth(newWidth);
            scrollBar.setLayoutX((tabPane.getWidth() - newWidth) / 2);
            event.consume();
        });
    }
}
