package vinnsla;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SafeCasinoController {
    private PlayerProfile profile = new PlayerProfile(1000); // Shared player profile



    @FXML
    private void startBlackjack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vinnsla/BlackJack.fxml"));
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
    @FXML
    private void startRoulette(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vinnsla/Roulette.fxml"));
            Parent root = loader.load();

            RouletteController controller = loader.getController();
            controller.setProfile(profile);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Roulette");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

