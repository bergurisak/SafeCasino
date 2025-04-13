package vinnsla;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MenuController {
    private PlayerProfile profile = new PlayerProfile(1000);

    public void setProfile(PlayerProfile profile) {
        this.profile = profile;
    }

    @FXML
    private void closeHandler() {
        System.exit(0);
    }

    @FXML
    private void BlackjackRules() {
        showAlert("Blackjack Game Rules", """
                Goal: Get closer to 21 than the dealer without going over.

                Card Values:
                2–10 = face value
                J, Q, K = 10
                Ace = 1 or 11

                Game Start: You and dealer get 2 cards.

                Player Options:
                Hit = take a card
                Stand = keep your hand

                Dealer Hits until reaching 17 or more.
                Bust = over 21
                Win = closer to 21 or dealer busts.
                """);
    }

    @FXML
    private void SlotsRules() {
        showAlert("Slot Game Rules", """
                Match 3 symbols:
                🍒 Cherry → 3× bet
                🍉 Melon → 4× bet
                🍋 Lemon → 5× bet
                🔔 Bell → 10× bet
                ⭐ Star → 20× bet

                Match 2 symbols:
                🍒 → 2×
                🍉 → 3×
                🍋 → 4×
                🔔 → 5×
                ⭐ → 10×

                🎵 Spin the reels, land matches, and win big!
                """);
    }

    @FXML
    private void BackToMenu(ActionEvent event) {
        loadScene("/vinnsla/SafeCasino.fxml", event, "SafeCasino");
    }

    @FXML
    private void startBlackjack(ActionEvent event) {
        loadSceneWithProfile("/vinnsla/BlackJack.fxml", event, "Blackjack");
    }

    @FXML
    private void startRoulette(ActionEvent event) {
        loadSceneWithProfile("/vinnsla/Roulette.fxml", event, "Roulette");
    }

    @FXML
    private void startSlots(ActionEvent event) {
        loadSceneWithProfile("/vinnsla/Slots.fxml", event, "Slots");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadScene(String fxmlPath, ActionEvent event, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSceneWithProfile(String fxmlPath, ActionEvent event, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof BlackJackController blackjack) {
                blackjack.setProfile(profile);
            } else if (controller instanceof RouletteController roulette) {
                roulette.setProfile(profile);
            } else if (controller instanceof SlotsController slots) {
                slots.setProfile(profile);
            }

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
