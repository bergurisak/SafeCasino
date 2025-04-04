package vinnsla;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class RouletteController implements Initializable {
    private PlayerProfile profile;

    public void setProfile(PlayerProfile profile) {
        this.profile = profile;
        System.out.println("Profile set: $" + profile.getBalance());
        updateBetDisplay();

    }

    @FXML
    private void BackToMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vinnsla/SafeCasino.fxml"));
            Parent root = loader.load();

            SafeCasinoController controller = loader.getController();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SafeCasino");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateBetDisplay() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
