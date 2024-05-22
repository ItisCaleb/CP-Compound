package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.utils.FileTreeCell;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;

public class FileTreeViewController {
    @FXML
    private TreeView<String> fileTreeView;
    @FXML
    private ToggleButton toggleExpandCollapseButton;
    @FXML
    private Button openFolderButton;
    @FXML
    private Tooltip openFolderTooltip,expandCollapseTooltip;
    @FXML
    private void handleOpenFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");
        File selectedDirectory = directoryChooser.showDialog(fileTreeView.getScene().getWindow());
        if (selectedDirectory != null) {
            loadDirectoryIntoTreeView(selectedDirectory);
        }
    }
    private void loadDirectoryIntoTreeView(File dir) {
//        System.out.println("loadDirectoryIntoTreeView");
        TreeItem<String> root = createTreeItem(dir);
        fileTreeView.setRoot(root);
    }
    private TreeItem<String> createTreeItem(File file) {
        TreeItem<String> item = new TreeItem<>(file.getName());

        if (file.isDirectory()) {

            TreeItem<String> dummy = new TreeItem<>("Loading...");
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

    private void loadChildItems(TreeItem<String> item, File dir) {
//        System.out.println("loadChildITtems: dir = " + dir);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                TreeItem<String> childItem = new TreeItem<>(f.getName());
                if (f.isDirectory()) {
                    childItem.getChildren().add(new TreeItem<>("Loading..."));
                    setupLazyLoad(childItem, f);
                }
                item.getChildren().add(childItem);
            }
        }
    }

    private void setupLazyLoad(TreeItem<String> item, File dir) {
        item.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
            if (isNowExpanded && item.getChildren().size() == 1 && "Loading...".equals(item.getChildren().get(0).getValue())) {
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
    private void expandAll(TreeItem<String> item) {
        if(item.isLeaf()){
            return;
        }
        if (!item.isLeaf() && !item.isExpanded()) {
            item.setExpanded(true);
        }
        for (TreeItem<String> child : item.getChildren()) {
            expandAll(child);
        }
    }
    private void collapseAll(TreeItem<String> item) {
        item.setExpanded(false);
        for (TreeItem<String> child : item.getChildren()) {
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
    @FXML
    public void initialize() {
        initIcons();
        setTooltipsDelay();
        fileTreeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
            @Override
            public TreeCell<String> call(TreeView<String> tv) {
                return new FileTreeCell();
            }
        });
        System.out.println("FileTreeViewController initialize");
    }
}
