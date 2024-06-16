package com.itiscaleb.cpcompound.component;

import com.itiscaleb.cpcompound.CPCompound;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.boxicons.BoxiconsSolid;
import org.kordamp.ikonli.dashicons.Dashicons;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class FileTreeCell extends TreeCell<File> {
    private ContextMenu contextMenu = new ContextMenu();
    public FileTreeCell() {
        setUpContexMenu();
    }
    private void setUpContexMenu(){
        MenuItem newFileItem = new MenuItem("New File");
        newFileItem.setOnAction(event -> createNewFile(getTreeItem()));

        MenuItem newFolderItem = new MenuItem("New Folder");
        newFolderItem.setOnAction(event -> createNewFolder(getTreeItem()));

        MenuItem renameItem = new MenuItem("Rename");
        renameItem.setOnAction(event -> renameItem(getTreeItem()));

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(event -> deleteItem(getTreeItem()));

        contextMenu.getItems().addAll(newFileItem, newFolderItem, renameItem, deleteItem);

        setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY && getTreeItem()!=null) {
                contextMenu.show(this, event.getScreenX(), event.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
    }
    @Override
    protected void updateItem(File item, boolean empty) {
        super.updateItem(item, empty);
        getStyleClass().remove("tree-cell-dummy");
        if (empty || item==null) {
            setText(null);
            setGraphic(null);
        } else if(item.getName().equals("Loading...")){
            getStyleClass().remove("tree-cell-fill");
            getStyleClass().add("tree-cell-dummy");
        }else {

//            getStyleClass().add("tree-cell-fill");
            setText(item.getName());
            updateGraphic(getTreeItem());
        }
    }

    private void updateGraphic(TreeItem<File> treeItem) {
        if(treeItem.isLeaf() && treeItem.getValue().isDirectory()) {
            treeItem.getChildren().add(new TreeItem<>(new File("Loading...")));
        }
//        if(treeItem.getValue().getName().equals("Loading...")) {
//            System.out.println("cell height: "+getHeight());
//            getStyleClass().remove("tree-cell-fill");
//            getStyleClass().add("tree-cell-dummy");
//            System.out.println("cell height: "+getHeight());
//            return;
//        }
//        getStyleClass().remove("tree-cell-dummy");
//        getStyleClass().add("tree-cell-fill");
        FontIcon arrowIcon = new FontIcon(treeItem.isExpanded() ?Dashicons.ARROW_DOWN_ALT2 : Dashicons.ARROW_RIGHT_ALT2);
        arrowIcon.setIconColor(Color.valueOf("#b6aeae"));
        arrowIcon.setIconSize(13);
        arrowIcon.setTranslateY(5);
        arrowIcon.setTranslateY(6);
        arrowIcon.setTranslateX(8);
        setDisclosureNode(arrowIcon);

        if (treeItem.getValue() != null) {
            if (!treeItem.getValue().isDirectory()) {
//                System.out.println("updateGraphic: " + treeItem.getValue());
                FontIcon icon = new FontIcon(BoxiconsSolid.FILE);
                icon.setIconColor(Color.valueOf("#B3CED8FF"));
                setGraphic(icon);
            } else if(treeItem.getValue().isDirectory()){
                FontIcon icon = treeItem.isExpanded() ?  new FontIcon(FontAwesomeSolid.FOLDER_OPEN) :  new FontIcon(FontAwesomeSolid.FOLDER);
                icon.setIconColor(Color.valueOf("#BF9659FF"));
                setGraphic(icon);
            }
        } else {
            setGraphic(null);
        }
    }
    private void createNewFile(TreeItem<File> parentItem) {
        CPCompound.getLogger().warn("Creating new file");
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New File");
        dialog.setHeaderText("Enter name for the new file:");
        dialog.setContentText("File name:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.isEmpty()) {
                File parentDir = parentItem.getValue();
                if(parentDir.isFile()){
                    parentDir=parentDir.getParentFile();
                }
                File newFile = new File(parentDir, name);
                if (newFile.exists()) {
                    showErrorDialog("Error", "A file with this name already exists.");
                } else {
                    try {
                        newFile.createNewFile();
//                        if (newFile.createNewFile()) {
//                            parentItem.getChildren().add(new TreeItem<>(newFile));
//                        }
                    } catch (IOException e) {
                        showErrorDialog("Error", "Failed to create the file.");
                    }
                }
            }
        });
    }
    private void createNewFolder(TreeItem<File> parentItem) {
        CPCompound.getLogger().warn("Create New Folder");
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New Folder");
        dialog.setHeaderText("Enter name for the new folder:");
        dialog.setContentText("Folder name:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.isEmpty()) {
                File parentDir = parentItem.getValue();
                if(parentDir.isFile()){
                    parentDir=parentDir.getParentFile();
                }
                System.out.println("parentItem: " + parentItem.getValue());
                File newFolder = new File(parentDir, name);
                if (newFolder.exists()) {
                    showErrorDialog("Error", "A folder with this name already exists.");
                } else {
                    try{
                        newFolder.mkdir();
                    }catch(SecurityException e){
                        showErrorDialog("Error", "Failed to create the folder.");
                    }
                }
            }
        });
    }

    private void renameItem(TreeItem<File> item) {
        CPCompound.getLogger().warn("renameItem: "+item.getValue());
        TextInputDialog dialog = new TextInputDialog(item.getValue().getName());
        dialog.setTitle("Rename Item");
        dialog.setHeaderText("Rename the file or folder");
        dialog.setContentText("Enter new name:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.isEmpty()) {
                File file = item.getValue();
                File renamedFile = new File(file.getParentFile(), name);
                if (renamedFile.exists()) {
                    showErrorDialog("Error", "File or folder already exists.");
                } else {
                    try {
                        if(!file.isDirectory()){
                            System.out.println("file rename");
                            file.renameTo(renamedFile);
                        }else{
                            System.out.println("folder rename");
//                            file.renameTo(renamedFile);
                            renamedFile.mkdir();
                            moveContents(file, renamedFile);
                            file.delete();
//                            deleteRecursive(file);
                        }
//                        System.out.println("before rename:"+file.getName());
//                        System.out.println("is success? "+file.renameTo(renamedFile));
//                        file.renameTo(renamedFile);
//                        System.out.println("after rename:"+file.getName());
                    } catch (SecurityException e) {
                        showErrorDialog("Error", "Failed to rename the file or folder.");
                    }
                }
            }
        });
    }
    private static void moveContents(File src, File dest) {
        File[] files = src.listFiles();

        for (File file : files) {
            File newFile = new File(dest, file.getName());
            if (file.isDirectory()) {
                newFile.mkdir();
                moveContents(file, newFile);
                file.delete(); // 刪除原始子文件夾
            } else {
                file.renameTo(newFile);
            }
        }
    }
    private void deleteItem(TreeItem<File> item) {
        CPCompound.getLogger().warn("deleteItem: " + item.getValue());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this item?");
        if (item.getValue().isDirectory()) {
            alert.setContentText("Deleting this folder will remove all contained files and cannot be undone.");
        } else {
            alert.setContentText("This action cannot be undone.");
        }

        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == confirmButton) {
                try {
                    System.out.println("deleteItem: " + item.getValue());
                    deleteRecursive(item.getValue());
                }catch(SecurityException e){
                    showErrorDialog("Error", "Failed to delete.");
                }
            }
        });
    }

    private boolean deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File child : Objects.requireNonNull(file.listFiles())) {
                deleteRecursive(child);
            }
        }
        System.out.println("deleteRecursive: " + file.getAbsolutePath());
        return file.delete();
    }

    private void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
