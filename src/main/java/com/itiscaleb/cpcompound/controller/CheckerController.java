package com.itiscaleb.cpcompound.controller;

import com.itiscaleb.cpcompound.utils.TestcaseCompare;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CheckerController {
    @FXML
    private VBox testCaseBox;
    @FXML
    private CheckBox strictMatchCheckBox;
    @FXML
    private Label testCaseLabel;

    private List<TestCase> testCases;
    private int testCaseCount;

    public void initialize() {
        testCases = new ArrayList<>();
        testCaseCount = 1;
    }

    @FXML
    private void addTestCase() {
        TestCase testCase = new TestCase(testCaseCount);
        testCases.add(testCase);
        testCaseBox.getChildren().add(testCase.getPane());
        testCaseCount++;
    }

    @FXML
    private void runComparisons() {
        boolean strictMatch = strictMatchCheckBox.isSelected();
        for (TestCase testCase : testCases) {
            testCase.runComparison(strictMatch);
        }
    }

    private class TestCase {
        private int number;
        private TextField inputField;
        private TextField expectedField;
        private TextField receivedField;
        private Label resultLabel;
        private VBox pane;
        private boolean current;

        public TestCase(int number) {
            this.number = number;
            inputField = new TextField();
            expectedField = new TextField();
            receivedField = new TextField();
            resultLabel = new Label("Result: ");
            current = false;

            pane = new VBox(10);
            pane.setPadding(new Insets(5));
            pane.getChildren().addAll(
                    new Label("Testcase" + number + ":"),
                    new Label("Input:"),
                    inputField,
                    new Label("Expected Output:"),
                    expectedField,
                    new Label("Received Output:"),
                    receivedField,
                    resultLabel
            );

            Button recompareButton = new Button("重新比对此测试用例");
            recompareButton.setOnAction(e -> recompareTestCase());

            Button deleteButton = new Button("删除此测试用例");
            deleteButton.setOnAction(e -> deleteTestCase());

            HBox buttonsBox = new HBox(10);
            buttonsBox.getChildren().addAll(recompareButton, deleteButton);
            pane.getChildren().add(0, buttonsBox);
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

        public void runComparison(boolean strictMatch) {
            String expected = getExpectedOutput();
            String received = getReceivedOutput();
            if (strictMatch) {
                resultLabel.setText("Result: " + ((new TestcaseCompare()).strictMatch(expected, received) ? "PASS" : "FAIL"));
            } else {
                resultLabel.setText("Result: " + ((new TestcaseCompare()).nonStrictMatch(expected, received) ? "PASS" : "FAIL"));
            }
        }

        public void recompareTestCase() {
            String input = inputField.getText();
            String expected = expectedField.getText();
            String received = receivedField.getText();
            if (input.isEmpty() || expected.isEmpty() || received.isEmpty()) {
                // Show an alert or handle the case where any of the fields is empty
                return;
            }

            runComparison(strictMatchCheckBox.isSelected());
        }

        public void deleteTestCase() {
            Iterator<TestCase> iterator = testCases.iterator();
            while (iterator.hasNext()) {
                TestCase testCase = iterator.next();
                if (testCase.isCurrent()) {
                    iterator.remove();
                    testCaseBox.getChildren().remove(testCase.getPane());
                    break;
                }
            }
        }

        public boolean isCurrent() {
            return current;
        }
    }
}
