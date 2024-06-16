package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.component.FileTreeCell;
import com.itiscaleb.cpcompound.utils.Config;
import com.itiscaleb.cpcompound.utils.FileChangeHandler;
import com.itiscaleb.cpcompound.utils.FileWatcherService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class FileTreeViewController implements FileChangeHandler {
    static FileTreeViewController instance;
    @FXML
    private TreeView<File> fileTreeView;
    @FXML
    private ToggleButton toggleExpandCollapseButton;
    @FXML
    private Button openFolderButton;
    @FXML
    private Tooltip openFolderTooltip,expandCollapseTooltip;
    private FileWatcherService fileWatcherService;
    public File selectedDirectory;
    @FXML
    private void handleOpenFolder() throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");
        selectedDirectory = directoryChooser.showDialog(fileTreeView.getScene().getWindow());
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
            Arrays.sort(files, Comparator.comparing(File::isFile).thenComparing(File::getName));
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
            if (isNowExpanded && item.getChildren().size() == 1 && "Loading...".equals(item.getChildren().get(0).getValue().getName())) {
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
        if (item.getValue().isDirectory() && !item.isExpanded()) {
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
        if(event.getClickCount() == 2) {
            TreeItem<File> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                File file = selectedItem.getValue();
                if (file.isDirectory()) return;
                EditorController.getInstance().addNewFile(file);
                BaseController.getInstance().showMessageToUser("Opened file: \"" + file.getPath() + "\"");
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
        System.out.println(CPCompound.getConfig().last_open_directory);
        Path path = Paths.get(CPCompound.getConfig().last_open_directory);
        System.out.println("last_open_directory: " + path);

        if(CPCompound.getConfig().last_open_directory != null) {
            try {
                fileWatcherService = new FileWatcherService(path);
                fileWatcherService.registerHandler(this);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        CPCompound.getLogger().info("FileTreeViewController initialize");
    }


    @Override
    public void onFileCreated(Path path) {
        CPCompound.getLogger().info("FileTreeViewController register onFileCreated, path: " + path);
        Platform.runLater(() -> {
            File file = path.toFile();

            TreeItem<File> parentItem = findTreeItem(fileTreeView.getRoot(), file.getParentFile());
            System.out.println("parentItem:"+parentItem.getValue());
            for(TreeItem<File> childItem : parentItem.getChildren()) {
                if(childItem.getValue().getName().equals(file.getName())) {
                    System.out.println("replace: "+childItem.getValue());
                    file = childItem.getValue();
                }
            }

            System.out.println("getRoot: " + fileTreeView.getRoot().getValue());
            if (parentItem != null && !containsChild(parentItem, file)) {
                System.out.println("find :"+file.getPath());
                TreeItem<File> newItem = new TreeItem<>(file);
                System.out.println("created file: " + file.getName()+" "+" "+file.isDirectory());
                if (file.isDirectory()) {
                    System.out.println("isDirectory");
//                    newItem.;
                    newItem.getChildren().add(new TreeItem<>(new File("Loading...")));
                }
//                else {
                    parentItem.getChildren().add(newItem);
//                }
//                parentItem.setExpanded(true);
            }
        });
    }

    @Override
    public void onFileDeleted(Path path) {
        CPCompound.getLogger().info("FileTreeViewController register onFileDeleted, path: " + path);
        Platform.runLater(() -> {
            File file = path.toFile();
            TreeItem<File> itemToDelete = findTreeItem(fileTreeView.getRoot(),file);
            System.out.println("itemToDelete:"+itemToDelete.getValue());
            if (itemToDelete.getParent() != null) {
                System.out.println("delete:"+file.getPath());
                deleteRecursive(itemToDelete);
            }
        });
    }
    private boolean deleteRecursive(TreeItem<File> item) {
        if (!item.isLeaf()) {
            for (TreeItem<File> child : Objects.requireNonNull(item.getChildren())) {
                deleteRecursive(child);
            }
        }
        return item.getParent().getChildren().remove(item);
    }

    @Override
    public void onFileModified(Path path) {
        CPCompound.getLogger().info("FileTreeViewController register onFileModified, path: " + path);
        Platform.runLater(() -> {
            File file = path.toFile();
            System.out.println("getRoot: " + fileTreeView.getRoot().getValue());
            TreeItem<File> modifiedItem = findTreeItem(fileTreeView.getRoot(), file);
            if (modifiedItem != null) {
                System.out.println("modifiedItem:"+modifiedItem.getValue());
//                modifiedItem.getParent().getChildren().remove(modifiedItem);

//                modifiedItem.setValue(null);  // Trigger update
                modifiedItem.setValue(file);
            }
        });
    }

    private TreeItem<File> findTreeItem(TreeItem<File> current, File target) {
//        System.out.println("findTreeItem current: "+current.getValue()+" target: "+target);

//        if (current.getValue().getName().equals(target.getName())) {
//            System.out.println("findTreeItem found");
//            return current;
//        }
        if(current.getValue().getName().equals("Loading...")){
            return current;
        }
        if (current.getValue().equals(target)) {
//            System.out.println("findTreeItem found");
            return current;
        }
//        System.out.println("childs: ");
//        for (TreeItem<File> child : current.getChildren()) {
//            System.out.print(child.getValue().getName()+" ");
//        }
//        System.out.println();
        TreeItem<File> result=null;
        for (TreeItem<File> child : current.getChildren()) {

//            if(!child.isLeaf()) {
//                TreeItem<File> result = findTreeItem(child, target);
//                if (child.getValue().equals(target)){
//                    System.out.println("findTreeItem found: "+child.getValue().getName());
//                    return child;
//                }
           result = findTreeItem(child, target);
            if (result.getValue().equals(target)) {
//                System.out.println("findTreeItem found: "+child.getValue());
                return result;
            }
//            }else if(child.getValue().getName().equals(target.getName())) {
//                return child;
//            }
        }

        return current;
    }

    private boolean containsChild(TreeItem<File> parentItem, File childFile) {
        return parentItem.getChildren().stream().anyMatch(item -> item.getValue().equals(childFile));
    }

}
