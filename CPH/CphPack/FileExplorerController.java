package CphPack;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileExplorerController {
    @FXML
    private TreeView<String> treeView;

    private TreeItem<String> rootItem;
    private Stage primaryStage;

    @FXML
    public void initialize() {
        rootItem = new TreeItem<>("Root");
        rootItem.setExpanded(true);
        treeView.setRoot(rootItem);
        treeView.getStyleClass().add("tree-view"); // 添加样式类
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void handleSelectFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory != null) {
            rootItem.getChildren().clear();
            buildFileTree(selectedDirectory, rootItem);
            rootItem.setValue(selectedDirectory.getAbsolutePath());
        }
    }

    private void buildFileTree(File dir, TreeItem<String> parentItem) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                TreeItem<String> item = new TreeItem<>(file.getName());
                parentItem.getChildren().add(item);
                if (file.isDirectory()) {
                    buildFileTree(file, item);
                }
            }
        } else {
            System.out.println("No files found in " + dir.getAbsolutePath());
        }
    }
}
