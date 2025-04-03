package vinnsla;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SafeCasinoApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SafeCasinoApplication.class.getResource("SafeCasino.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 888, 700);
        stage.setTitle("Safe Casino!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {launch();
    }
}