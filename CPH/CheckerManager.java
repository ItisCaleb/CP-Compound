import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class CheckerManager extends Application {
    private List<TestCase> testCases;
    private VBox testCaseBox;
    private TextArea resultArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        testCases = new ArrayList<>();

        primaryStage.setTitle("String Comparator");

        // Main layout
        BorderPane mainPane = new BorderPane();

        // Panel for test cases
        testCaseBox = new VBox(10);
        testCaseBox.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(testCaseBox);
        scrollPane.setFitToWidth(true);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        Button addButton = new Button("Add Test Case");
        Button runButton = new Button("Run Comparisons");
        buttonBox.getChildren().addAll(addButton, runButton);

        // Result area
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefRowCount(10);

        mainPane.setTop(buttonBox);
        mainPane.setCenter(scrollPane);
        mainPane.setBottom(resultArea);

        // Add button action
        addButton.setOnAction(e -> addTestCase());

        // Run button action
        runButton.setOnAction(e -> runComparisons());

        Scene scene = new Scene(mainPane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addTestCase() {
        TestCase testCase = new TestCase();
        testCases.add(testCase);
        testCaseBox.getChildren().add(testCase.getPane());
    }

    private void runComparisons() {
        StringBuilder results = new StringBuilder();
        for (TestCase testCase : testCases) {
            String expected = testCase.getExpected();
            String actual = testCase.getActual();
            if (expected.equals(actual)) {
                results.append("PASS: ").append(expected).append(" == ").append(actual).append("\n");
            } else {
                results.append("FAIL: ").append(expected).append(" != ").append(actual).append("\n");
            }
        }
        resultArea.setText(results.toString());
    }

    private class TestCase {
        private TextField expectedField;
        private TextField actualField;
        private HBox pane;

        public TestCase() {
            expectedField = new TextField();
            actualField = new TextField();
            Button removeButton = new Button("Remove");

            pane = new HBox(10);
            pane.setPadding(new Insets(5));
            pane.getChildren().addAll(new Label("Expected:"), expectedField, new Label("Actual:"), actualField, removeButton);

            // Remove button action
            removeButton.setOnAction(e -> {
                testCases.remove(this);
                testCaseBox.getChildren().remove(pane);
            });
        }

        public HBox getPane() {
            return pane;
        }

        public String getExpected() {
            return expectedField.getText();
        }

        public String getActual() {
            return actualField.getText();
        }
    }
}
