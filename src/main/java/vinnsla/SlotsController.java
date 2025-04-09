package vinnsla;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.effect.GaussianBlur;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class SlotsController {
    private PlayerProfile profile;
    private int currentBet = 0;
    private AudioClip spinSound;

    @FXML
    private Label balanceLabel;
    @FXML
    private TextField betField;
    @FXML
    private Label resultLabel;
    @FXML
    private ImageView slot1;
    @FXML
    private ImageView slot2;
    @FXML
    private ImageView slot3;

    private final String[] symbolNames = {"cherry", "melon", "lemon", "bell", "star"};
    //private final String[] symbols = {"🍒", "🍉", "🍋", "🔔", "⭐"};

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

        profile.decreaseBalance(currentBet);

        String[] row = spinRow();
        animateReels(row);
        playSound("spin4.mp3");

        int payout = getPayout(row, currentBet);

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
        ImageView[] reels = {slot1, slot2, slot3};
        Random rand = new Random();

        for (int i = 0; i < 3; i++) {
            final int index = i;
            final ImageView reel = reels[index];

            GaussianBlur blur = new GaussianBlur(10);
            reel.setEffect(blur);

            TranslateTransition shake = new TranslateTransition(Duration.millis(80), reel);
            shake.setFromX(-3);
            shake.setToX(3);
            shake.setCycleCount(Animation.INDEFINITE);
            shake.setAutoReverse(true);
            shake.play();

            Timeline spin = new Timeline();
            int spinFrames = 50 + index * 10;
            int frameDelay = 25;
            int totalDuration = spinFrames * frameDelay;

            for (int j = 0; j < spinFrames; j++) {
                int delay = j * frameDelay;
                spin.getKeyFrames().add(new KeyFrame(Duration.millis(delay), e -> {
                    String randomSymbol = symbolNames[rand.nextInt(symbolNames.length)];
                    showSymbolImage(reel, randomSymbol);
                }));
            }

            spin.getKeyFrames().add(new KeyFrame(Duration.millis(totalDuration), e -> {
                showSymbolImage(reel, finalRow[index]);

                GaussianBlur currentBlur = (GaussianBlur) reel.getEffect();

                Timeline blurFade = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(currentBlur.radiusProperty(), 10)),
                        new KeyFrame(Duration.millis(250), new KeyValue(currentBlur.radiusProperty(), 0))
                );
                blurFade.setOnFinished(evt -> reel.setEffect(null));
                blurFade.play();

                shake.stop();
                reel.setTranslateX(0);
            }));

            spin.play();
        }
    }

    private void showSymbolImage(ImageView imageView, String symbolName) {
        try {
            String path = "/images/" + symbolName + ".png";
            Image image = new Image(getClass().getResource(path).toExternalForm(), true);
            imageView.setImage(image);
            imageView.setSmooth(true);
            System.out.println("Spinning: " + symbolName);
        } catch (Exception e) {
            System.out.println("Failed to load image: " + symbolName);
            e.printStackTrace();
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
            if (fileName.equals("spin2.mp3") && spinSound != null) {
                spinSound.stop();
            }

            AudioClip sound = new AudioClip(getClass().getResource("/mp3/" + fileName).toExternalForm());
            if (fileName.equals("spin2.mp3")) {
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
                case "cherry"-> bet * 3;
                case "melon" -> bet * 4;
                case "lemon" -> bet * 5;
                case "bell"  -> bet * 10;
                case "star"  -> bet * 20;
                default -> 0;
            };
        } else if (row[0].equals(row[1]) || row[1].equals(row[2])) {
            String match = row[0].equals(row[1]) ? row[0] : row[1];
            return switch (match) {
                case "cherry"-> bet * 2;
                case "melon" -> bet * 3;
                case "lemon" -> bet * 4;
                case "bell"  -> bet * 5;
                case "star"  -> bet * 10;
                default -> 0;
            };
        }
        return 0;
    }

    private String[] spinRow() {
        String[] row = new String[3];
        Random rand = new Random();
        for (int i = 0; i < 3; i++) {
            row[i] = symbolNames[rand.nextInt(symbolNames.length)];
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
