package CphPack;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private Pane fileExplorerPane;
    private Pane mainPane;
    private BorderPane rootPane;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        rootPane = new BorderPane();

        // 创建主界面
        mainPane = FXMLLoader.load(getClass().getResource("Demo.fxml"));
        rootPane.setCenter(mainPane);

        // 创建文件浏览器界面
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FileExplorer.fxml"));
        fileExplorerPane = loader.load();
        FileExplorerController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);

        // 创建按钮并添加到顶部
        Button switchButton = new Button("Switch to File Explorer");
        switchButton.setOnAction(e -> switchPane());
        rootPane.setTop(switchButton);

        Scene scene = new Scene(rootPane, 800, 600);
        scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Main Application");
        primaryStage.show();
    }

    private void switchPane() {
        if (rootPane.getCenter() == mainPane) {
            rootPane.setCenter(fileExplorerPane);
            ((Button) rootPane.getTop()).setText("Switch to Main Pane");
        } else {
            rootPane.setCenter(mainPane);
            ((Button) rootPane.getTop()).setText("Switch to File Explorer");
        }
    }

    @Override
    public void init() throws Exception {
        super.init();
        System.out.println("CphPack init");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("CphPack stop");
    }
}
