package vinnsla;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SafeCasinoController {
    private PlayerProfile profile = new PlayerProfile(1000); // Shared player profile

    @FXML private Button blackjackButton;

    @FXML
    private void startBlackjack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BlackJack.fxml"));
            Parent root = loader.load();

            BlackJackController controller = loader.getController();
            controller.setProfile(profile);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Blackjack");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

