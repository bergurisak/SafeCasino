package vinnsla;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Random;

public class SlotsController {

    private PlayerProfile profile;

    public void setProfile(PlayerProfile profile) {
        this.profile = profile;
        updateBalanceDisplay();
    }

    @FXML private Label balanceLabel;
    @FXML private TextField betField;
    @FXML private Label resultLabel;
    @FXML private Text slotDisplay;

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

    private final String[] symbols = {"🍒", "🍉", "🍋", "🔔", "⭐"};

    @FXML
    private void spin() {
        if (profile == null) {
            resultLabel.setText("Profile not loaded.");
            return;
        }

        int bet;
        try {
            bet = Integer.parseInt(betField.getText());
            if (bet <= 0) {
                resultLabel.setText("Bet must be greater than 0.");
                return;
            }
            if (bet > profile.getBalance()) {
                resultLabel.setText("Insufficient balance.");
                return;
            }
        } catch (NumberFormatException e) {
            resultLabel.setText("Enter a valid number.");
            return;
        }

        profile.decreaseBet(bet); // deduct bet
        String[] row = spinRow();
        slotDisplay.setText(String.join(" ", row));

        int payout = getPayout(row, bet);
        if (payout > 0) {
            profile.increaseBalance(payout);
            resultLabel.setText("You won $" + payout + "!");
        } else {
            resultLabel.setText("Sorry, you lost this round.");
        }

        updateBalanceDisplay();
        betField.clear();
    }

    private void updateBalanceDisplay() {
        balanceLabel.setText("Balance: $" + profile.getBalance());
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
}
