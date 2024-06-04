package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.component.FileTreeCell;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;

public class FileTreeViewController {
    static FileTreeViewController instance;
    @FXML
    private TreeView<File> fileTreeView;
    @FXML
    private ToggleButton toggleExpandCollapseButton;
    @FXML
    private Button openFolderButton;
    @FXML
    private Tooltip openFolderTooltip,expandCollapseTooltip;
    @FXML
    private void handleOpenFolder() throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");
        File selectedDirectory = directoryChooser.showDialog(fileTreeView.getScene().getWindow());
        if (selectedDirectory != null) {
            loadDirectoryIntoTreeView(selectedDirectory);
            CPCompound.getConfig().last_open_directory = selectedDirectory.getCanonicalPath();
            CPCompound.getConfig().save();
        }
    }

    public static FileTreeViewController getInstance() {
        return instance;
    }

    public void loadDirectoryIntoTreeView(File dir) {
        TreeItem<File> root = createTreeItem(dir);
        fileTreeView.setRoot(root);
    }
    private TreeItem<File> createTreeItem(File file) {
        TreeItem<File> item = new TreeItem<>(file);
        if (file.isDirectory()) {
            TreeItem<File> dummy = new TreeItem<>(new File("Loading..."));
            item.getChildren().add(dummy);
            item.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
                if (isNowExpanded && item.getChildren().contains(dummy)) {
                    item.getChildren().remove(dummy);
                    loadChildItems(item, file);
                }
            });
        }
        return item;
    }

    private void loadChildItems(TreeItem<File> item, File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                TreeItem<File> childItem = new TreeItem<>(f);
                if (f.isDirectory()) {
                    childItem.getChildren().add(new TreeItem<>(new File("Loading...")));
                    setupLazyLoad(childItem, f);
                }
                item.getChildren().add(childItem);
            }
        }
    }

    private void setupLazyLoad(TreeItem<File> item, File dir) {
        item.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
            if (isNowExpanded && item.getChildren().size() == 1 && "Loading...".equals(item.getChildren().get(0).getValue().getPath())) {
                item.getChildren().clear();
                loadChildItems(item, dir);
            }
        });
    }
    @FXML
    private void handleToggleExpandCollapse() {
        if (toggleExpandCollapseButton.isSelected()) {
            expandCollapseTooltip.setText("Collapse All");
        } else {
            expandCollapseTooltip.setText("Expand All");
        }
        if (fileTreeView.getRoot() != null) {
            if (toggleExpandCollapseButton.isSelected()) {
                expandAll(fileTreeView.getRoot());
                expandCollapseTooltip.setText("Collapse All");
            } else {
                collapseAll(fileTreeView.getRoot());
                expandCollapseTooltip.setText("Expand All");
            }
        }
    }
    private void expandAll(TreeItem<File> item) {
        if(item.isLeaf()){
            return;
        }
        if (!item.isLeaf() && !item.isExpanded()) {
            item.setExpanded(true);
        }
        for (TreeItem<File> child : item.getChildren()) {
            expandAll(child);
        }
    }
    private void collapseAll(TreeItem<File> item) {
        item.setExpanded(false);
        for (TreeItem<File> child : item.getChildren()) {
            collapseAll(child);
        }
    }
    private void initIcons() {
        toggleExpandCollapseButton.setGraphic(new FontIcon());
        openFolderButton.setGraphic(new FontIcon());
    }
    private void setTooltipsDelay(){
        openFolderTooltip.setShowDelay(javafx.util.Duration.seconds(0));
        openFolderTooltip.setHideDelay(javafx.util.Duration.seconds(0));
        openFolderTooltip.setShowDuration(javafx.util.Duration.seconds(10));
        expandCollapseTooltip.setShowDelay(javafx.util.Duration.seconds(0));
        expandCollapseTooltip.setHideDelay(javafx.util.Duration.seconds(0));
        expandCollapseTooltip.setShowDuration(javafx.util.Duration.seconds(10));
    }
    private void setTreeItemListener(){
        fileTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            handleTreeItemClick(event);
        });
    }
    private void handleTreeItemClick(MouseEvent event) {
        if(event.getClickCount() == 2){
            TreeItem<File> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
            File file = selectedItem.getValue();
            if(file.isDirectory()) return;
            if (selectedItem != null) {
                EditorController.getInstance().addNewFile(file);
            }
        }
    }
    @FXML
    public void initialize() {
        instance = this;
        initIcons();
        setTooltipsDelay();
        setTreeItemListener();
        fileTreeView.setCellFactory(tv -> new FileTreeCell());
        CPCompound.getLogger().info("FileTreeViewController initialize");
    }
}
