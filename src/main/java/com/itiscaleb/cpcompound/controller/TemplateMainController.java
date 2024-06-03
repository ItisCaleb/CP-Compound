package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.component.EditableLabel;
import com.itiscaleb.cpcompound.component.TemplateItem;
import com.itiscaleb.cpcompound.utils.APPData;
import com.itiscaleb.cpcompound.utils.TemplateManager;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.kordamp.ikonli.dashicons.Dashicons;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateMainController {
    @FXML
    Button addCategoryBtn;
    @FXML
    VBox templateVBox;
    TemplateManager templateManager;
    Map<String, ArrayList<TemplateItem>> templates;
    @FXML
    private void handleAddCategory(){
        System.out.println("Add Category");
        try {
            Path templateFolderPath = APPData.resolve("Code Template");
            String baseName = "untitle";
            int maxNumber = 0;
            boolean baseExists = false;

            // Using DirectoryStream to find all matching folders
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(templateFolderPath, baseName + "*")) {
                Pattern pattern = Pattern.compile(baseName + "(\\d*)");
                for (Path entry : stream) {
                    Matcher matcher = pattern.matcher(entry.getFileName().toString());
                    if (matcher.matches()) {
                        if (matcher.group(1).isEmpty()) {
                            baseExists = true;  // The base untitle directory exists
                        } else {
                            maxNumber = Math.max(maxNumber, Integer.parseInt(matcher.group(1)));
                        }
                    }
                }
            }

            // Determine the new directory name
            String newDirName = baseExists ? baseName + (maxNumber + 1) : baseName;
            Path newDirPath = templateFolderPath.resolve(newDirName);
            Files.createDirectory(newDirPath);

            // Refresh UI
            addNewCategoryUI(newDirName);
            //update templates in TabManager
            templates.put(newDirName,new ArrayList<>());
            //update order in TabManager
            templateManager.pushBackOrder(newDirName);
            System.out.println("Added new category: " + newDirName);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to add category due to IO error.");
        }
    }
    private void addNewCategoryUI(String newDirName){
        VBox categoryContent = new VBox(5); // Spacing between items
        categoryContent.getStyleClass().add("category-content");
        VBox contentArea = new VBox(5);
        contentArea.getStyleClass().add("contentArea");
        HBox categoryHeader = createCategoryHeader(contentArea,newDirName);
        categoryContent.getChildren().addAll(categoryHeader,contentArea);
        templateVBox.getChildren().add(categoryContent);
    }
    private void loadTemplatesUI(){
        templateVBox.getChildren().clear();
        templates = templateManager.getTemplatesByOrder();
        for (Map.Entry<String, ArrayList<TemplateItem>> entry : templates.entrySet()) {
            String categoryName = entry.getKey();
            ArrayList<TemplateItem> items = entry.getValue();
            VBox categoryContent = new VBox(5); // Spacing between items
            categoryContent.getStyleClass().add("category-content");
            VBox contentArea = new VBox(5);
            contentArea.getStyleClass().add("contentArea");
            HBox categoryHeader = createCategoryHeader(contentArea,categoryName);
            for (TemplateItem item : items) {
                VBox itemInfo = createItemInfoHBox(item,categoryName);
                contentArea.getChildren().add(itemInfo);
            }
            categoryContent.getChildren().addAll(categoryHeader,contentArea);
            templateVBox.getChildren().add(categoryContent);
        }
    }
    private HBox createCategoryHeader(VBox contentArea,String categoryName){
        ToggleButton collapseBtn = new ToggleButton();
        collapseBtn.setGraphic(new FontIcon());
        collapseBtn.getStyleClass().add("collapse-btn");
        collapseBtn.setSelected(true);
        contentArea.setVisible(false);
        contentArea.setManaged(false);
        FontIcon rightArrow = new FontIcon(Dashicons.ARROW_RIGHT_ALT2);
        FontIcon downArrow = new FontIcon(Dashicons.ARROW_DOWN_ALT2);
        collapseBtn.setGraphic(rightArrow);
        collapseBtn.setOnAction(event -> {
            if(collapseBtn.isSelected()){
                contentArea.setVisible(false);
                contentArea.setManaged(false);
                collapseBtn.setGraphic(rightArrow);
            }else{
                contentArea.setVisible(true);
                contentArea.setManaged(true);
                collapseBtn.setGraphic(downArrow);
            }
        });

        EditableLabel headerLabel = new EditableLabel();
        setupEditableLabel(headerLabel,categoryName);
        headerLabel.getStyleClass().add("header-label");
        headerLabel.setText(categoryName);


        Button addItemBtn = new Button();
        addItemBtn.setGraphic(new FontIcon());
        addItemBtn.getStyleClass().add("add-item-btn");
        addItemBtn.setOnAction(event -> handleAddItem(categoryName,contentArea));

        Button deleteItemBtn = new Button();
        deleteItemBtn.setGraphic(new FontIcon());
        deleteItemBtn.getStyleClass().add("delete-item-btn");

        HBox categoryHeader=new HBox(5);
        categoryHeader.getStyleClass().add("header-hbox");
        HBox headerBtnArea=new HBox(5);
        headerBtnArea.getStyleClass().add("header-btn-area");

        deleteItemBtn.setOnAction(event -> handleDeleteCategory(categoryName,categoryHeader));
        headerBtnArea.getChildren().addAll(addItemBtn,deleteItemBtn);
        headerBtnArea.setAlignment(Pos.CENTER_RIGHT);
        categoryHeader.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(headerBtnArea,Priority.ALWAYS);
        categoryHeader.getChildren().addAll(collapseBtn,headerLabel,headerBtnArea);
        HBox.setHgrow(headerLabel,Priority.ALWAYS);
        return categoryHeader;
    }
    private VBox createItemInfoHBox(TemplateItem item,String categoryName) {
        VBox itemInfo = new VBox(2); // Spacing between elements
        itemInfo.getStyleClass().add("item-info");
        HBox itemMainInfo = new HBox(10);
        itemMainInfo.getStyleClass().add("item-main-info");
        HBox itemSubInfo = new HBox(10);
        itemSubInfo.getStyleClass().add("item-sub-info");

        EditableLabel itemName = new EditableLabel();
        itemName.setText(item.getFileName());
        itemName.getStyleClass().add("item-name");

        HBox infoBtnArea =new HBox(5);
        infoBtnArea.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(infoBtnArea,Priority.ALWAYS);
        infoBtnArea.getStyleClass().add("info-btn-area");
        infoBtnArea.setTranslateY(8);
        Button openItemBtn = new Button();
        openItemBtn.setGraphic(new FontIcon());
        openItemBtn.getStyleClass().add("open-item-btn");
        openItemBtn.setOnAction(event -> handleOpenItem(item,categoryName));

        Button removeItemBtn = new Button();
        removeItemBtn.setGraphic(new FontIcon());
        removeItemBtn.getStyleClass().add("remove-item-btn");
        removeItemBtn.setOnAction(event -> handleRemoveItem(item,categoryName,itemInfo));

        Button copyCodeBtn = new Button();
        copyCodeBtn.setGraphic(new FontIcon());
        copyCodeBtn.getStyleClass().add("copy-code-btn");
        copyCodeBtn.setOnAction(event -> {
            copyCodeBtn.getStyleClass().removeAll("copy-code-btn");
            copyCodeBtn.getStyleClass().add("copy-code-btn-click");
            handleCopyCode(item);
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> {
                copyCodeBtn.getStyleClass().removeAll("copy-code-btn-click");
                copyCodeBtn.getStyleClass().add("copy-code-btn");
            });
            pause.play();
        });
        infoBtnArea.getChildren().addAll(copyCodeBtn,openItemBtn,removeItemBtn);

        itemMainInfo.getChildren().addAll(itemName,infoBtnArea);

        Label itemLineCount = new Label(String.valueOf(item.getLineCount())+" lines");
        itemLineCount.getStyleClass().add("item-line-count");

        Label fileExtension = new Label("type: "+item.getExtension());
        fileExtension.getStyleClass().add("file-extension");

        Label lastUpdateDate = new Label("edit "+item.getLastModified());
        lastUpdateDate.getStyleClass().add("last-update-date");
        itemInfo.setOnMouseEntered(event -> {itemName.getLabel().setStyle("-fx-text-fill: #FFFFFF");});
        itemInfo.setOnMouseExited(event -> {itemName.getLabel().setStyle("-fx-text-fill: #A49F9F");});

        itemSubInfo.getChildren().addAll(itemLineCount,fileExtension, lastUpdateDate);
        itemInfo.getChildren().addAll(itemMainInfo, itemSubInfo);

        return itemInfo;
    }

    public void setupEditableLabel(EditableLabel editableLabel, String oldDirectoryName) {
        editableLabel.setOnCommit(() -> {
            String newDirectoryName = editableLabel.getText();
            if(renameDirectory(oldDirectoryName, newDirectoryName)){
                setupEditableLabel(editableLabel, newDirectoryName);
            }else{
                editableLabel.setText(oldDirectoryName);
            }
        });
    }

    private boolean renameDirectory(String oldName, String newName) {
        System.out.println("newName: "+newName);
        System.out.println("oldName: "+oldName);
        Path source = APPData.resolve("Code Template/"+oldName);
        Path target = APPData.resolve("Code Template/"+newName);
        if (newName.equals("Code Template")) {
            showAlert("Rename Error", "You can't rename 'Code Template' in Code Template");
            return false;
        }
        try {
            Files.move(source, target);
            templates.put(newName,templates.get(oldName));
            templates.remove(oldName);
            //update order in templateManager
            templateManager.updateOrderByName(oldName,newName);

        }catch (FileAlreadyExistsException e){
            showAlert("Rename Error", "A folder named '" + newName + "' already exists. Please choose a different name.");
            return false;
        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("error");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.show();
    }
    private boolean showConfirmDeleteCategoryAlert(){
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("warning");
        dialog.setHeaderText("Sure to delete the category?");
        dialog.setContentText("Are you sure you want to delete this category?\nAll the files in category will disappear");
        ButtonType confirmButtonType = new ButtonType("Confirm");
        ButtonType cancelButtonType = new ButtonType("Cancel");
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, cancelButtonType);
        final boolean[] isConfirmed = new boolean[1];
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                System.out.println("Confirm button clicked");
                isConfirmed[0] = true;
                return "Confirmed";
            } else if (dialogButton == cancelButtonType) {
                System.out.println("Cancel button clicked");
                isConfirmed[0] = false;
                return "Cancelled";
            }
            return null;
        });
        dialog.showAndWait();
        return isConfirmed[0];

    }

    private void handleDeleteCategory(String categoryName,HBox categoryHeader){
        boolean isConfirm = showConfirmDeleteCategoryAlert();
        if(!isConfirm){
            return;
        }
        VBox parentVBox = (VBox) categoryHeader.getParent();
        templateVBox.getChildren().remove(parentVBox);
        templates.remove(categoryName);
        deleteCategoryDirectory(categoryName);
        templateManager.removeOrderByName(categoryName);
        System.out.println("handleDeleteCategory");
    }
    private void deleteCategoryDirectory(String categoryName) {
        try {
            Path directoryPath =APPData.resolve("Code Template/"+categoryName);
            System.out.println("try delete category: "+directoryPath);
            Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean showConfirmDeleteItemAlert(){
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("warning");
        dialog.setHeaderText("Sure to delete the item?");
        dialog.setContentText("Are you sure you want to delete item?\nThe item will delete forever");
        ButtonType confirmButtonType = new ButtonType("Confirm");
        ButtonType cancelButtonType = new ButtonType("Cancel");
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, cancelButtonType);
        final boolean[] isConfirmed = new boolean[1];
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                System.out.println("Confirm button clicked");
                isConfirmed[0] = true;
                return "Confirmed";
            } else if (dialogButton == cancelButtonType) {
                System.out.println("Cancel button clicked");
                isConfirmed[0] = false;
                return "Cancelled";
            }
            return null;
        });
        dialog.showAndWait();
        return isConfirmed[0];

    }
    private void handleRemoveItem(TemplateItem item,String categoryName,VBox itemInfo) {
        System.out.println("handleremoveItem");
        boolean isConfirm = showConfirmDeleteItemAlert();
        if(!isConfirm){
            System.out.println("cancel remove item");
            return;
        }
        VBox parentContentArea = (VBox) itemInfo.getParent();
        parentContentArea.getChildren().remove(itemInfo);
        Path filePath = APPData.resolve("Code Template/"+categoryName+"/"+item.getFileName());
        try{
            Files.deleteIfExists(filePath);
            ArrayList<TemplateItem> items = templateManager.getTemplates().get(categoryName);
            items.remove(item);
        }catch (IOException e) {
            showAlert("Error", "Failed to delete the file" );
        }
    }
    private void showAlert(String title) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText("Please try again:)");
        alert.showAndWait();
    }
    private void handleOpenItem(TemplateItem item,String categoryName) {
        Path filePaht = APPData.resolve("Code Template/"+categoryName+"/"+item.getFileName());
        File openFile = filePaht.toFile();
        EditorController.getInstance().addNewFile(openFile);
        System.out.println("Open "+openFile.getName());
    }
    private void handleCopyCode(TemplateItem item) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(item.getFileContent());
        clipboard.setContent(content);
    }
    private Integer showNewFileDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New File");

        ButtonType copyFromFileButton = new ButtonType("Copy from Folder", ButtonBar.ButtonData.OK_DONE);
        ButtonType addEmptyFileButton = new ButtonType("Add Empty File", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(copyFromFileButton, addEmptyFileButton, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        final Integer[] chooseWay = new Integer[1];
        result.ifPresent(response -> {
            if (response == copyFromFileButton) {
                chooseWay[0] = 1;
            } else if (response == addEmptyFileButton) {
                chooseWay[0] = 2;
            }else{
                chooseWay[0] = 3;
            }
        });
        return chooseWay[0];
    }
    private void copyFromFile(String categoryName,VBox contentArea) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Copy");
        File file = fileChooser.showOpenDialog(null);
        if (file != null && isTextFile(file.getName())) {
            try {
                Path targetDirectory = APPData.resolve("Code Template/"+categoryName);
                Files.copy(file.toPath(), targetDirectory.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
                TemplateItem item= addTemplateItemToManager(file, targetDirectory);
                addTemplateItemToContentArea(contentArea,item,categoryName);
            } catch (IOException e) {
                showAlert("Error", "Failed to copy file.");
            }
        } else {
            showAlert("Error", "Invalid file type or operation cancelled.");
        }
    }
    private void createEmptyFile(String categoryName,VBox contentArea) {
        List<String> options = Arrays.asList("text", "cpp", "c", "py");
        ChoiceDialog<String> typeDialog = new ChoiceDialog<>("text", options);
        typeDialog.setTitle("Select File Type");
        typeDialog.setHeaderText("Choose a file type for the new file:");
        Optional<String> typeResult = typeDialog.showAndWait();

        typeResult.ifPresent(type -> {
            Path targetDirectory = APPData.resolve("Code Template/"+categoryName);
            String baseName = "untitle";
            String extension = "." + type;
            File file = findUniqueFileName(targetDirectory, baseName, extension);
            try {
                Files.createFile(file.toPath());
                TemplateItem item= addTemplateItemToManager(file, targetDirectory);
                addTemplateItemToContentArea(contentArea,item,categoryName);
            } catch (IOException e) {
                showAlert("Error", "Failed to create file.");
            }
        });
    }

    private File findUniqueFileName(Path directory, String baseName, String extension) {
        int num = 0;
        while (true) {
            File file = new File(directory.resolve(baseName + (num > 0 ? num : "") + extension).toString());
            if (!file.exists()) return file;
            num++;
        }
    }
    private boolean isTextFile(String fileName) {
        return fileName.endsWith(".cc") || fileName.endsWith(".cpp") || fileName.endsWith(".c") || fileName.endsWith(".py") || fileName.endsWith(".txt");
    }
    private void handleAddItem(String categoryName, VBox contentArea) {
        Integer chooseWay = showNewFileDialog();
        if(chooseWay == 1){
            copyFromFile(categoryName,contentArea);
        }else if(chooseWay == 2){
            createEmptyFile(categoryName,contentArea);
        }else{
            return;
        }
        System.out.println("handleAddItem");
    }
    private void addTemplateItemToContentArea(VBox contentArea,TemplateItem item,String categoryName) {
        VBox itemInfo = createItemInfoHBox(item,categoryName);
        contentArea.getChildren().add(itemInfo);
    }
    private TemplateItem addTemplateItemToManager(File file, Path directory) {
        TemplateItem item=null;
        try {
            // Read file content
            String content = new String(Files.readAllBytes(file.toPath()));
            String extension = templateManager.getFileExtension(file.toPath());
            LocalDateTime lastModified = LocalDateTime.ofInstant(
                    Files.getLastModifiedTime(file.toPath()).toInstant(),
                    ZoneId.systemDefault()
            );

            // Create a new TemplateItem
            item = new TemplateItem(
                    file.getName(),
                    extension,
                    content,
                    templateManager.formatRelativeTime(lastModified)
            );

            // Assume directory.getName() returns the category name under which this file should be classified
            templates.get(directory.getFileName().toString()).add(item);

        } catch (IOException e) {
            System.out.println("Error reading file or adding item: " + e.getMessage());
            showAlert("Error", "Failed to read file content or add template item.");
        }
        return item;
    }


    private void initTemplateManager() throws IOException{
        //build templateManager through "code template" folder
        templateManager = new TemplateManager();
//        templateManager.displayTemplates();
    }
    private void initIcon(){
        addCategoryBtn.setGraphic(new FontIcon());
    }
    public void initialize() throws IOException {
        Path NewtemplatePath = APPData.resolve("Code Template");
        System.out.println("path: " + NewtemplatePath);
        if(!Files.exists(NewtemplatePath)) {
            System.out.println("creating folder: Code Template");
            Files.createDirectory(NewtemplatePath);
        }else{
            System.out.println("folder already exists");
        }
        initTemplateManager();
        loadTemplatesUI();
        initIcon();

    }


}
