package vinnsla;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Random;

public class SlotsController {

    private PlayerProfile profile;
    private int currentBet = 0;

    @FXML private Label balanceLabel;
    @FXML private TextField betField;
    @FXML private Label resultLabel;
    @FXML private Text slotDisplay;

    private final String[] symbols = {"🍒", "🍉", "🍋", "🔔", "⭐"};

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

    public void setProfile(PlayerProfile profile) {
        this.profile = profile;
        updateBalanceDisplay();
    }

    @FXML
    private void setBet() {
        try {
            int bet = Integer.parseInt(betField.getText());

            if (bet <= 0) {
                resultLabel.setText("Bet must be greater than 0.");
                return;
            }

            if (profile.getBalance() < bet) {
                resultLabel.setText("Insufficient balance for that bet.");
                return;
            }

            currentBet = bet;
            resultLabel.setText("Bet set to $" + currentBet + ". Now spin!");
            betField.clear();

        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter a valid number.");
        }
    }
    private void celebrateWin(String message) {
        resultLabel.setText(message);
        resultLabel.setTextFill(Color.GOLD);

        // Scale pop animation
        ScaleTransition scale = new ScaleTransition(Duration.seconds(1.0), resultLabel);
        scale.setFromX(1);
        scale.setFromY(1);
        scale.setToX(1.5);
        scale.setToY(1.5);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);

        // Fade in/out animation
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), resultLabel);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setCycleCount(2);
        fade.setAutoReverse(true);

        // Reset text color after delay
        PauseTransition resetColor = new PauseTransition(Duration.seconds(2));
        resetColor.setOnFinished(e -> resultLabel.setTextFill(Color.BLACK));

        // Play all
        scale.play();
        fade.play();
        resetColor.play();
    }

    @FXML
    private void spin() {
        if (profile == null) {
            resultLabel.setText("Profile not loaded.");
            return;
        }

        if (currentBet <= 0) {
            resultLabel.setText("Set your bet first.");
            return;
        }

        if (profile.getBalance() < currentBet) {
            resultLabel.setText("Insufficient balance to spin.");
            return;
        }

        profile.decreaseBalance(currentBet);


        String[] row = spinRow();
        slotDisplay.setText(String.join(" ", row));

        int payout = getPayout(row, currentBet);

        if (payout > 0) {
            profile.increaseBalance(payout);
            celebrateWin("You won $" + payout + "!");
        } else {
            resultLabel.setText("Sorry, you lost.");
        }

        updateBalanceDisplay();
    }

    private String[] spinRow() {
        String[] row = new String[3];
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            row[i] = symbols[random.nextInt(symbols.length)];
        }
        return row;
    }

    private int getPayout(String[] row, int bet) {
        if (row[0].equals(row[1]) && row[1].equals(row[2])) {
            return switch (row[0]) {
                case "🍒" -> bet * 3;
                case "🍉" -> bet * 4;
                case "🍋" -> bet * 5;
                case "🔔" -> bet * 10;
                case "⭐" -> bet * 20;
                default -> 0;
            };
        } else if (row[0].equals(row[1]) || row[1].equals(row[2])) {
            String match = row[0].equals(row[1]) ? row[0] : row[1];
            return switch (match) {
                case "🍒" -> bet * 2;
                case "🍉" -> bet * 3;
                case "🍋" -> bet * 4;
                case "🔔" -> bet * 5;
                case "⭐" -> bet * 10;
                default -> 0;
            };
        }
        return 0;
    }

    private void updateBalanceDisplay() {
        balanceLabel.setText("Balance: $" + profile.getBalance());
    }
}
