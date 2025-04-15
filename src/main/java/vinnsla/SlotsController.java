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
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.effect.GaussianBlur;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SlotsController {
    private static final Logger logger = Logger.getLogger(SlotsController.class.getName());

    private final PlayerProfile profile = CasinoSession.getProfile();
    private int currentBet = 0;
    private boolean spinning = false;
    private MediaPlayer backgroundPlayer;

    private Timeline autoSpinTimeline;
    private boolean isAutoSpinning = false;

    @FXML private AnchorPane gamePane;
    @FXML private Label balanceLabel;
    @FXML private TextField betField;
    @FXML private Label resultLabel;
    @FXML private Button spinButton;
    @FXML private ImageView slot1, slot2, slot3;
    @FXML private Label betAmountLabel;
    @FXML private Button autoSpinButton;

    private final String[] symbolNames = {"cherry", "melon", "lemon", "bell", "star"};

    @FXML
    public void initialize() {
        updateBalanceDisplay();
        startBackgroundMusic();
    }

    private void startBackgroundMusic() {
        try {
            Media media = new Media(getClass().getResource("/mp3/CasinoSoundEffect.mp3").toExternalForm());
            backgroundPlayer = new MediaPlayer(media);
            backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundPlayer.setVolume(0.3);
            backgroundPlayer.setOnEndOfMedia(() -> backgroundPlayer.seek(Duration.ZERO));
            backgroundPlayer.play();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to play background music", e);
        }
    }

    @FXML
    private void setBet() {
        try {
            int bet = Integer.parseInt(betField.getText());
            if (bet <= 0 || profile.getBalance() < bet) {
                resultLabel.setText("Invalid bet.");
                return;
            }
            currentBet = bet;
            betAmountLabel.setText("Bet: $" + currentBet);
            resultLabel.setText("Bet set to $" + currentBet);
            betField.clear();
        } catch (NumberFormatException e) {
            resultLabel.setText("Enter a valid number.");
        }
    }

    @FXML
    private void spin() {
        if (spinning || currentBet <= 0 || profile.getBalance() < currentBet) return;

        spinning = true;
        spinButton.setDisable(true);
        profile.decreaseBalance(currentBet);
        playSound("spin5.mp3");

        String[] row = spinRow();
        animateReels(row);

        PauseTransition pause = new PauseTransition(Duration.millis(2000));
        pause.setOnFinished(e -> {
            int payout = getPayout(row, currentBet);
            if (payout > 0) {
                profile.increaseBalance(payout);
                playSound("winSlots.mp3");
                celebrateWin("You won $" + payout + "!");
            } else {
                resultLabel.setText("Sorry, you lost.");
            }
            updateBalanceDisplay();
            if (profile.getBalance() <= 0) showBankruptImage();
            spinning = false;
            spinButton.setDisable(false);
        });
        pause.play();
    }

    private void animateReels(String[] finalRow) {
        ImageView[] reels = {slot1, slot2, slot3};
        Random rand = new Random();

        for (int i = 0; i < 3; i++) {
            final int index = i;
            ImageView reel = reels[index];
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
                Timeline blurFade = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(blur.radiusProperty(), 10)),
                        new KeyFrame(Duration.millis(250), new KeyValue(blur.radiusProperty(), 0))
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
            Image image = new Image(getClass().getResource("/images/" + symbolName + ".png").toExternalForm(), true);
            imageView.setImage(image);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Image load failed for: " + symbolName, e);
        }
    }

    private void celebrateWin(String message) {
        resultLabel.setText(message);
        resultLabel.setTextFill(Color.GOLD);

        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.3), resultLabel);
        scale.setFromX(1); scale.setFromY(1); scale.setToX(1.5); scale.setToY(1.5);
        scale.setCycleCount(2); scale.setAutoReverse(true);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), resultLabel);
        fade.setFromValue(0); fade.setToValue(1);
        fade.setCycleCount(2); fade.setAutoReverse(true);

        PauseTransition resetColor = new PauseTransition(Duration.seconds(2));
        resetColor.setOnFinished(e -> resultLabel.setTextFill(Color.BLACK));

        scale.play(); fade.play(); resetColor.play();
    }

    private void playSound(String fileName) {
        try {
            AudioClip sound = new AudioClip(getClass().getResource("/mp3/" + fileName).toExternalForm());
            sound.play();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Sound failed: " + fileName, e);
        }
    }

    private int getPayout(String[] row, int bet) {
        if (row[0].equals(row[1]) && row[1].equals(row[2])) {
            return switch (row[0]) {
                case "cherry" -> bet * 3;
                case "melon" -> bet * 4;
                case "lemon" -> bet * 5;
                case "bell" -> bet * 10;
                case "star" -> bet * 20;
                default -> 0;
            };
        } else if (row[0].equals(row[1]) || row[1].equals(row[2])) {
            String match = row[0].equals(row[1]) ? row[0] : row[1];
            return switch (match) {
                case "cherry" -> bet * 2;
                case "melon" -> bet * 3;
                case "lemon" -> bet * 4;
                case "bell" -> bet * 5;
                case "star" -> bet * 10;
                default -> 0;
            };
        }
        return 0;
    }

    private String[] spinRow() {
        Random rand = new Random();
        return new String[]{
                symbolNames[rand.nextInt(symbolNames.length)],
                symbolNames[rand.nextInt(symbolNames.length)],
                symbolNames[rand.nextInt(symbolNames.length)]
        };
    }

    private void updateBalanceDisplay() {
        balanceLabel.setText("Balance: $" + profile.getBalance());
    }

    private void showBankruptImage() {
        try {
            Image image = new Image(getClass().getResource("/images/broke.png").toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(400);
            imageView.setPreserveRatio(true);
            imageView.setLayoutX(300);
            imageView.setLayoutY(200);
            imageView.setMouseTransparent(true);

            gamePane.getChildren().add(imageView);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), imageView);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            PauseTransition pause = new PauseTransition(Duration.seconds(3));

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), imageView);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            fadeOut.setOnFinished(e -> gamePane.getChildren().remove(imageView));

            new SequentialTransition(fadeIn, pause, fadeOut).play();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to show bankrupt image", e);
        }
    }

    private void stopAutoSpin() {
        isAutoSpinning = false;
        autoSpinButton.setText("Auto Spin");
        if (autoSpinTimeline != null) autoSpinTimeline.stop();
    }

    @FXML
    private void BackToMenu(ActionEvent event) {
        try {
            if (backgroundPlayer != null) backgroundPlayer.stop();
            stopAutoSpin();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vinnsla/SafeCasino.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SafeCasino");
            stage.show();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to return to menu", e);
        }
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
                """);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(message);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("""
    -fx-background-color: #002d06;
    -fx-border-color: gold;
    -fx-border-width: 2px;
    -fx-font-size: 14px;
    -fx-text-fill: white;
    -fx-font-family: 'Times New Roman', Times, serif;
    """);
        dialogPane.lookupButton(ButtonType.OK).setStyle("""
    -fx-background-color: gold;
    -fx-font-family: 'Times New Roman', Times, serif;
    -fx-text-fill: black;
    -fx-font-weight: bold;
    -fx-border-color: white;
    -fx-border-width: 1px;
    """);
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: gold; -fx-font-size: 15px; -fx-font-family: 'Times New Roman', Times, serif; -fx-font-weight: bold;");
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/cherry.png")));
        alert.showAndWait();
    }

    private void startAutoSpin() {
        isAutoSpinning = true;
        autoSpinButton.setText("Stop Auto");

        autoSpinTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            if (!spinning && profile.getBalance() >= currentBet) {
                spin();
            } else {
                stopAutoSpin();
            }
        }));
        autoSpinTimeline.setCycleCount(Animation.INDEFINITE);
        autoSpinTimeline.play();
    }
    @FXML
    private void toggleAutoSpin(ActionEvent event) {
        if (isAutoSpinning) {
            stopAutoSpin();
        } else {
            startAutoSpin();
        }
    }
}