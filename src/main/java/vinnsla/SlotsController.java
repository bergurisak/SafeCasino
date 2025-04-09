package vinnsla;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class SlotsController {

    private PlayerProfile profile;
    private int currentBet = 0;

    private AudioClip spinSound;

    @FXML private Label balanceLabel;
    @FXML private TextField betField;
    @FXML private Label resultLabel;
    @FXML private Text slot1;
    @FXML private Text slot2;
    @FXML private Text slot3;

    private final String[] symbols = {"🍒", "🍉", "🍋", "🔔", "⭐"};

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

        // Deduct bet from balance
        profile.decreaseBalance(currentBet);

        // Spin the slots
        String[] row = spinRow();
        animateReels(row);
        playSound("spin.mp3");

        int payout = getPayout(row, currentBet);

        // Delay result until animation completes
        PauseTransition pause = new PauseTransition(Duration.millis(1200));
        pause.setOnFinished(e -> {
            if (payout > 0) {
                profile.increaseBalance(payout);
                playSound("winSlots.mp3");
                celebrateWin("You won $" + payout + "!");
            } else {
                resultLabel.setText("Sorry, you lost.");
            }
            updateBalanceDisplay();
        });
        pause.play();
    }

    private void animateReels(String[] finalRow) {
        Text[] reels = {slot1, slot2, slot3};
        Random rand = new Random();

        for (int i = 0; i < 3; i++) {
            Text reel = reels[i];
            int index = i;
            Timeline timeline = new Timeline();

            for (int j = 0; j < 15 + index * 5; j++) {
                int delay = j * 50;
                timeline.getKeyFrames().add(new KeyFrame(Duration.millis(delay), e -> {
                    reel.setText(symbols[rand.nextInt(symbols.length)]);
                }));
            }

            timeline.getKeyFrames().add(new KeyFrame(Duration.millis((15 + index * 5) * 50), e -> {
                reel.setText(finalRow[index]);
            }));

            timeline.play();
        }
    }

    private void celebrateWin(String message) {
        resultLabel.setText(message);
        resultLabel.setTextFill(Color.GOLD);

        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.3), resultLabel);
        scale.setFromX(1);
        scale.setFromY(1);
        scale.setToX(1.5);
        scale.setToY(1.5);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), resultLabel);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setCycleCount(2);
        fade.setAutoReverse(true);

        PauseTransition resetColor = new PauseTransition(Duration.seconds(2));
        resetColor.setOnFinished(e -> resultLabel.setTextFill(Color.BLACK));

        scale.play();
        fade.play();
        resetColor.play();
    }

    private void playSound(String fileName) {
        try {
            // Stop previous spin sound if it's playing
            if (fileName.equals("spin.mp3") && spinSound != null) {
                spinSound.stop();
            }

            AudioClip sound = new AudioClip(getClass().getResource("/mp3/" + fileName).toExternalForm());
            if (fileName.equals("spin.mp3")) {
                spinSound = sound;
            }

            sound.play();
        } catch (Exception e) {
            System.out.println("Sound failed: " + fileName);
            e.printStackTrace();
        }
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

    private String[] spinRow() {
        String[] row = new String[3];
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            row[i] = symbols[random.nextInt(symbols.length)];
        }
        return row;
    }

    private void updateBalanceDisplay() {
        balanceLabel.setText("Balance: $" + profile.getBalance());
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
}
