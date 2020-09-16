package main.java.com.github.PeterHausenAoi.evo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.java.com.github.PeterHausenAoi.evo.util.Log;

import java.io.FileInputStream;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader();

        Controller ctrl = new Controller();
        loader.setController(ctrl);

        URL path = this.getClass().getResource("sample.fxml");

        if(path == null){
            Log.doLog(Main.class.getSimpleName(), "Anim view not found.");
            System.exit(-1);
        }

        String fxmlDocPath = path.getPath();
        FileInputStream fxmlStream = new FileInputStream(fxmlDocPath);

        VBox root = loader.load(fxmlStream);
        root.setPadding(new Insets(10,10,10,10));
        Scene scene = new Scene(root);

        scene.setOnKeyPressed(ctrl::handleKeyEvent);

        stage.setScene(scene);
        stage.setTitle("Evo");
//        stage.setFullScreen(true);
        stage.setMaximized(true);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
