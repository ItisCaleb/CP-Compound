package com.itiscaleb.cpcompound.controller;

import com.google.gson.*;
import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.utils.TestcaseCompare;
import com.itiscaleb.cpcompound.editor.Editor;
import com.itiscaleb.cpcompound.editor.EditorContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.charset.Charset;

public class CheckerController {
    @FXML
    private VBox testCaseBox,checkerBase;
    @FXML
    private CheckBox strictMatchCheckBox;
    @FXML
    private Button addTestCaseBtn,runAllTestCaseBtn,deleteAllTestCaseBtn;

    private List<TestCase> testCases;
    private int testCaseCount;

    private Editor editor;
    private EditorContext editorContext;
    public String cph_path;
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public void updatePath() {
        editor = CPCompound.getEditor();
        editorContext = editor.getCurrentContext();
        cph_path = editorContext.getFileURI();
        testCases.clear();
        testCaseBox.getChildren().clear();
        testCaseCount = 1;
        loadTestCasesFromJson();
    }


    private void initIcons(){
        addTestCaseBtn.setGraphic(new FontIcon());
        runAllTestCaseBtn.setGraphic(new FontIcon());
        deleteAllTestCaseBtn.setGraphic(new FontIcon());
    }
    @FXML
    public void initialize() throws URISyntaxException {
        System.out.println("Initializing CheckerController");
        editor = CPCompound.getEditor();
        editorContext = editor.getCurrentContext();
        testCases = new ArrayList<>();
        testCaseCount = 1;
        initIcons();
        if (editorContext == null) {
            // UI please open cpp file
            // Yuankai ;)
            System.out.println("EditorContext is null");
            VBox forbiddenLabelBox = new VBox(10);
            HBox.setHgrow(forbiddenLabelBox, Priority.ALWAYS);
            forbiddenLabelBox.getStyleClass().add("forbidden-label-vbox");
            checkerBase.getChildren().clear();
            Label forbiddenLabel = new Label("Doesn't have any document associated with this checker\nPlease open a document:)");
            forbiddenLabel.getStyleClass().add("forbidden-label");
            forbiddenLabelBox.getChildren().add(forbiddenLabel);
            checkerBase.getChildren().addAll(forbiddenLabelBox);
        } else {
            cph_path = editorContext.getFileURI();
            createNewFolder(cph_path);
            loadTestCasesFromJson();
        }
    }

    private void loadTestCasesFromJson() {
        if (cph_path == null || cph_path.isEmpty()) {
            CPCompound.getLogger().error("No valid cph_path specified.");
            return;
        }
        try {
            URI uri = new URI(cph_path);
            String ccFilePath = Paths.get(uri).toString();
            String cphFolderPath = ccFilePath.substring(0, ccFilePath.lastIndexOf(File.separator) + 1) + "cph";
            String jsonFileName = cphFolderPath + File.separator + ccFilePath.substring(ccFilePath.lastIndexOf(File.separator) + 1, ccFilePath.lastIndexOf('.')) + ".json";
            Path jsonFilePath = Paths.get(jsonFileName);
            if (Files.exists(jsonFilePath)) {
                byte[] jsonData = Files.readAllBytes(jsonFilePath);
                String jsonString = new String(jsonData, Charset.defaultCharset());
                JsonArray testCaseArray = gson.fromJson(jsonString, JsonArray.class);
                for (JsonElement jsonElement : testCaseArray) {
                    JsonObject testCaseData = jsonElement.getAsJsonObject();
                    String input = testCaseData.get("input").getAsString();
                    String expectedOutput = testCaseData.get("expectedOutput").getAsString();
                    String receivedOutput = testCaseData.get("receivedOutput").getAsString();
                    String standardError =  testCaseData.get("standardError").getAsString();
                    TestCase testCase = new TestCase(testCaseCount, input, expectedOutput, receivedOutput,standardError);
                    testCases.add(testCase);
                    testCaseBox.getChildren().add(testCase.getPane());
                    testCaseCount++;
                }
                CPCompound.getLogger().info("Loaded test cases from JSON file: {}", jsonFileName);
                for (JsonElement jsonElement : testCaseArray) {
//                    JsonObject testCaseData = jsonElement.getAsJsonObject();
                    CPCompound.getLogger().info(jsonElement);
                }
            } else {
                CPCompound.getLogger().error("JSON file does not exist: " + jsonFileName);
            }
        } catch (IOException e) {
            CPCompound.getLogger().error("Error occurred", e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void createNewFolder(String filePath) {
        try {
            URI uri = new URI(filePath);
            String realPath = Paths.get(uri).toString();
            Path parentDirectory = Paths.get(realPath).getParent();
            if (parentDirectory != null) {
                String newFolderName = "cph";
                Path newFolderPath = parentDirectory.resolve(newFolderName);

                if (!Files.exists(newFolderPath)) {
                    Files.createDirectories(newFolderPath);
                }
            }
        } catch (URISyntaxException | IOException e) {
            CPCompound.getLogger().error("Error occurred", e);
        }
    }

    public void saveTestCasesToJson() {
        if (cph_path == null || cph_path.isEmpty()) {
            CPCompound.getLogger().error("No valid cph_path specified.");
            return;
        }
        try {
            URI uri = new URI(cph_path);
            String ccFilePath = Paths.get(uri).toString();
            String cphFolderPath = ccFilePath.substring(0, ccFilePath.lastIndexOf(File.separator) + 1) + "cph";
            Path cphFolder = Paths.get(cphFolderPath);
            if (!Files.exists(cphFolder)) {
                Files.createDirectories(cphFolder);
                CPCompound.getLogger().info("Created cph folder: " + cphFolder);
            }
            String jsonFileName = cphFolderPath + File.separator + ccFilePath.substring(ccFilePath.lastIndexOf(File.separator) + 1, ccFilePath.lastIndexOf('.')) + ".json";
            Path jsonFilePath = Paths.get(jsonFileName);
            if (!Files.exists(jsonFilePath)) {
                Files.createFile(jsonFilePath);
                CPCompound.getLogger().info("Created JSON file: " + jsonFilePath);
            }
            List<JsonObject> testCaseDataList = new ArrayList<>();
            for (TestCase testCase : testCases) {
                JsonObject testCaseData = new JsonObject();
                testCaseData.addProperty("input", testCase.getInput());
                testCaseData.addProperty("expectedOutput", testCase.getExpectedOutput());
                testCaseData.addProperty("receivedOutput", testCase.getReceivedOutput());
                testCaseData.addProperty("standardError", testCase.getStandardError());
                testCaseDataList.add(testCaseData);
            }
            Files.write(jsonFilePath, gson.toJson(testCaseDataList).getBytes());
            CPCompound.getLogger().info("Test cases saved to JSON file: {}", jsonFileName);
        } catch (URISyntaxException | IOException e) {
            CPCompound.getLogger().error("Error occurred", e);
        }
    }

    @FXML
    private void addTestCase() {
        TestCase testCase = new TestCase(testCaseCount,"","","","");
        testCases.add(testCase);
        testCaseBox.getChildren().add(testCase.getPane());
        testCaseCount++;
        renumberTestCases();
        saveTestCasesToJson();
    }

    @FXML
    private void deleteAllTestCase() {
        testCases.clear();
        testCaseBox.getChildren().clear();
        renumberTestCases();
        saveTestCasesToJson();
    }

    @FXML
    private void runAllTestCase() {
        saveTestCasesToJson();
        boolean strictMatch = strictMatchCheckBox.isSelected();
        CPCompound.getLogger().info("run all testcase");
        Editor editor = CPCompound.getEditor();
        editor.compile(editorContext, System.out, System.err).whenComplete((result, throwable) -> {
            if(!result.getValue()) return;
            EditorContext context = result.getKey();
            for (TestCase testCase : testCases) {
                InputStream inputStream = new ByteArrayInputStream(testCase.getInput().getBytes(Charset.defaultCharset()));
                //CPCompound.getBaseController().getEditorController().doExecute(inputStream);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
                editor.execute(context, inputStream, outputStream, errorStream, true).whenComplete((_result, _throwable) ->{
                    Platform.runLater(()->{
                            String s = outputStream.toString(StandardCharsets.UTF_8);
                            testCase.setReceivedField(s);
                            String se = errorStream.toString(StandardCharsets.UTF_8);
                            testCase.setStandardError(se);
                            testCase.runComparison(strictMatch);
                            saveTestCasesToJson();
                        }
                    );
                });
            }
        });
    }

    private void renumberTestCases() {
        int number = 1;
        for (TestCase testCase : testCases) {
            testCase.setNumber(number);
            number++;
        }
    }

    private class TestCase {
        private int number;
        private TextArea inputField;
        private TextArea expectedField;
        private TextArea receivedField;
        private TextArea errorField;
        private Label resultLabel;
        private Label errorLabel;
        private VBox pane;
        private boolean current;

        public TestCase(int number, String input, String expectedOutput, String receivedOutput,String standardError) {
            this.number = number;
            inputField = new TextArea(input);
            setUpTextArea(inputField);
            updateHeight(inputField);
            expectedField = new TextArea(expectedOutput);
            setUpTextArea(expectedField);
            updateHeight(expectedField);
            receivedField = new TextArea(receivedOutput);
            setUpTextArea(receivedField);
            updateHeight(receivedField);
            errorField = new TextArea(standardError);
            setUpTextArea(errorField);
            updateHeight(errorField);
            resultLabel = new Label("Result: ");
            resultLabel.getStyleClass().add("result-label");
            errorLabel = new Label("standard Error:");
            errorLabel.getStyleClass().add("error-label");
            current = false;

            pane = new VBox(10);
            pane.getStyleClass().add("test-case-pane");
            pane.setPadding(new Insets(5));

            if(expectedOutput.isEmpty()) {
                pane.getChildren().addAll(
                        new Label("Testcase " + number + ":"),
                        new Label("Input:"),
                        inputField,
                        new Label("Expected Output:"),
                        expectedField,
                        new Label("Received Output:"),
                        receivedField,
                        resultLabel
                );
            }else{
                pane.getChildren().addAll(
                        new Label("Testcase " + number + ":"),
                        new Label("Input:"),
                        inputField,
                        new Label("Expected Output:"),
                        expectedField,
                        new Label("Received Output:"),
                        receivedField,
                        errorLabel,
                        errorField,
                        resultLabel
                );
            }

            Button recompareButton = new Button("Run this");
            recompareButton.setOnAction(e -> compareOneTestCase());

            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> deleteOneTestCase());

            HBox buttonsBox = new HBox(10);
            buttonsBox.getStyleClass().add("buttons-box");
            buttonsBox.getChildren().addAll(recompareButton, deleteButton);
            pane.getChildren().add(0, buttonsBox);
        }
        public void setUpTextArea(TextArea textArea){
            System.out.println("set up "+ textArea.getText());
            textArea.setWrapText(false);
            textArea.textProperty().addListener((obs, oldText, newText) -> {
                updateHeight(textArea);
            });
        }
        private void updateHeight(TextArea textArea) {
            double lineHeight = 16;
            int numLines = textArea.getText().split("\n", -1).length;
            textArea.setPrefHeight(lineHeight * numLines );
        }
        public void setNumber(int number) {
            this.number = number;
            updatePane();
        }

        private void updatePane() {
            pane.getChildren().clear();

            pane.getChildren().addAll(
                    new Label("Testcase " + number + ":"),
                    new Label("Input:"),
                    inputField,
                    new Label("Expected Output:"),
                    expectedField,
                    new Label("Received Output:"),
                    receivedField,
                    errorLabel,
                    errorField,
                    resultLabel
            );

            Button recompareButton = new Button("Run this");
            recompareButton.setOnAction(e -> compareOneTestCase());

            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> deleteOneTestCase());

            HBox buttonsBox = new HBox(10);
            buttonsBox.getChildren().addAll(recompareButton, deleteButton);
            pane.getChildren().add(0, buttonsBox);
        }

        public void setReceivedField(String s){
            receivedField.setText(s);
        }

        public void setStandardError(String s) {
            if (s.isEmpty()) {
                pane.getChildren().remove(errorLabel);
                pane.getChildren().remove(errorField);
            } else {
                if (!pane.getChildren().contains(errorField)) {
                    int receivedOutputIndex = pane.getChildren().indexOf(receivedField);
                    pane.getChildren().add(receivedOutputIndex + 1, new Label("Standard Error:"));
                    pane.getChildren().add(receivedOutputIndex + 2, errorField);
                }
                errorField.setText(s);
            }
        }

        public VBox getPane() {
            return pane;
        }

        public String getInput() {
            return inputField.getText();
        }

        public String getExpectedOutput() {
            return expectedField.getText();
        }

        public String getReceivedOutput() {
            return receivedField.getText();
        }

        public String getStandardError() {
            return errorField.getText();
        }

        public void runComparison(boolean strictMatch) {
            String expected = getExpectedOutput();
            String received = getReceivedOutput();
            if (strictMatch) {
                resultLabel.setText("Result: " + ((new TestcaseCompare()).strictMatch(expected, received) ? "PASS" : "FAIL"));
            } else {
                resultLabel.setText("Result: " + ((new TestcaseCompare()).nonStrictMatch(expected, received) ? "PASS" : "FAIL"));
            }
        }

        public void compareOneTestCase() {
            CPCompound.getLogger().info("run one testcase");
            String input = inputField.getText();
            Editor editor = CPCompound.getEditor();
            editor.compile(editorContext ,System.out, System.err).whenComplete((result, throwable) -> {
                if(!result.getValue()) return;
                EditorContext context = result.getKey();
                InputStream inputStream = new ByteArrayInputStream(input.getBytes(Charset.defaultCharset()));
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
                editor.execute(context, inputStream, outputStream, errorStream, true).whenComplete((_result, _throwable) ->{
                    Platform.runLater(()->{
                                String s = outputStream.toString(StandardCharsets.UTF_8);
                                receivedField.setText(s);
                                String se = errorStream.toString(StandardCharsets.UTF_8);
                                errorField.setText(se);
                                runComparison(strictMatchCheckBox.isSelected());
                                saveTestCasesToJson();
                            }
                    );
                });
            });
        }

        public void deleteOneTestCase() {
            testCases.remove(this);
            testCaseBox.getChildren().remove(pane);
            renumberTestCases();
            saveTestCasesToJson();
        }

    }
}
