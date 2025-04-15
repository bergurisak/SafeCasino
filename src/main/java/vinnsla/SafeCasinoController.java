package vinnsla;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SafeCasinoController {
    private static final Logger logger = Logger.getLogger(SafeCasinoController.class.getName());

    private final PlayerProfile profile = CasinoSession.getProfile();

    @FXML
    private Label balanceLabel;

    @FXML
    public void initialize() {
        updateBalanceDisplay();
    }

    private void updateBalanceDisplay() {
        if (balanceLabel != null && profile != null) {
            balanceLabel.setText("Balance: $" + profile.getBalance());
        }
    }

    @FXML
    private void startBlackjack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vinnsla/BlackJack.fxml"));
            Parent root = loader.load();
            BlackJackController controller = loader.getController();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Blackjack");
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to start Blackjack", e);
        }
    }

    @FXML
    private void startSlots(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vinnsla/Slots.fxml"));
            Parent root = loader.load();
            SlotsController controller = loader.getController();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/star.png")));
            stage.setScene(new Scene(root));
            stage.setTitle("Slots");
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to start Slots", e);
        }
    }

    @FXML
    private void startRoulette(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vinnsla/Roulette.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Roulette");
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to start Roulette", e);
        }
    }
}