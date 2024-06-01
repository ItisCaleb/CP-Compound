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
    private VBox testCaseBox;
    @FXML
    private CheckBox strictMatchCheckBox;
    @FXML
    private Button testCaseLabel;

    private List<TestCase> testCases;
    private int testCaseCount;

    private Editor editor;
    private EditorContext editorContext;
    public String cph_path;
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public void updatePath() {
        System.out.println("update looking at");
        editor = CPCompound.getEditor();
        editorContext = editor.getCurrentContext();
        cph_path = editorContext.getFileURI();
        testCases.clear();
        testCaseBox.getChildren().clear();
        testCaseCount = 1;
        loadTestCasesFromJson();
    }



    public void initialize() throws URISyntaxException {
        editor = CPCompound.getEditor();
        editorContext = editor.getCurrentContext();

        testCases = new ArrayList<>();
        testCaseCount = 1;

        if (editorContext == null) {
            System.out.println("Please Open a file");
            // UI please open cpp file
            // Yuankai ;)
        } else {
            cph_path = editorContext.getFileURI();
            System.out.println(cph_path);
            createNewFolder(cph_path);
            loadTestCasesFromJson();
        }
    }

    private void loadTestCasesFromJson() {
        if (cph_path == null || cph_path.isEmpty()) {
            System.out.println("No valid cph_path specified.");
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
                System.out.println("Loaded test cases from JSON file: " + jsonFileName);
                for (JsonElement jsonElement : testCaseArray) {
//                    JsonObject testCaseData = jsonElement.getAsJsonObject();
                    System.out.println(jsonElement);
                }
            } else {
                System.out.println("JSON file does not exist: " + jsonFileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                    System.out.println("New folder created: " + newFolderPath);
                } else {
                    System.out.println("Folder already exists: " + newFolderPath);
                }
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTestCasesToJson() {
        if (cph_path == null || cph_path.isEmpty()) {
            System.out.println("No valid cph_path specified.");
            return;
        }
        try {
            URI uri = new URI(cph_path);
            String ccFilePath = Paths.get(uri).toString();
            String cphFolderPath = ccFilePath.substring(0, ccFilePath.lastIndexOf(File.separator) + 1) + "cph";
            Path cphFolder = Paths.get(cphFolderPath);
            if (!Files.exists(cphFolder)) {
                Files.createDirectories(cphFolder);
                System.out.println("Created cph folder: " + cphFolder);
            }
            String jsonFileName = cphFolderPath + File.separator + ccFilePath.substring(ccFilePath.lastIndexOf(File.separator) + 1, ccFilePath.lastIndexOf('.')) + ".json";
            Path jsonFilePath = Paths.get(jsonFileName);
            if (!Files.exists(jsonFilePath)) {
                Files.createFile(jsonFilePath);
                System.out.println("Created JSON file: " + jsonFilePath);
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
            System.out.println("Test cases saved to JSON file: " + jsonFileName);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addTestCase() {
        TestCase testCase = new TestCase(testCaseCount,"","","","");
        testCases.add(testCase);
        testCaseBox.getChildren().add(testCase.getPane());
        testCaseCount++;
        saveTestCasesToJson();
    }

    @FXML
    private void deleteAllTestCase() {
        testCases.clear();
        testCaseBox.getChildren().clear();
        saveTestCasesToJson();
    }

    @FXML
    private void runAllTestCase() {
        saveTestCasesToJson();
        boolean strictMatch = strictMatchCheckBox.isSelected();
        System.out.println("run all testcase");
        editorContext.compile(System.out, System.err).whenComplete((result, throwable) -> {
            if(!result.getValue()) return;
            EditorContext context = result.getKey();
            for (TestCase testCase : testCases) {
                InputStream inputStream = new ByteArrayInputStream(testCase.getInput().getBytes(Charset.defaultCharset()));
                //CPCompound.getBaseController().getEditorController().doExecute(inputStream);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
                context.execute(inputStream, outputStream, errorStream, true).whenComplete((_result, _throwable) ->{
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
            expectedField = new TextArea(expectedOutput);
            receivedField = new TextArea(receivedOutput);
            errorField = new TextArea(standardError);
            resultLabel = new Label("Result: ");
            errorLabel = new Label("standard Error:");
            current = false;

            pane = new VBox(10);
            pane.setPadding(new Insets(5));

            if(expectedOutput ==  "") {
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
            System.out.println("run one testcase");
            String input = inputField.getText();
            String expected = expectedField.getText();
            String received = receivedField.getText();

            editorContext.compile(System.out, System.err).whenComplete((result, throwable) -> {
                if(!result.getValue()) return;
                EditorContext context = result.getKey();
                InputStream inputStream = new ByteArrayInputStream(input.getBytes(Charset.defaultCharset()));
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
                context.execute(inputStream, outputStream, errorStream, true).whenComplete((_result, _throwable) ->{
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
            saveTestCasesToJson();
        }

    }
}
