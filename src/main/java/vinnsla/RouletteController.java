package vinnsla;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RouletteController implements Initializable {

    private static final Logger logger = Logger.getLogger(RouletteController.class.getName());

    @FXML private AnchorPane gamePane;
    @FXML private Button startButton;
    @FXML private Label balanceLabel;
    @FXML private Label betLabel;
    @FXML private Label resultLabel;
    @FXML private TextField betNumberField;
    @FXML private Button backButton;
    @FXML private Button increaseBetButton;
    @FXML private Button decreaseBetButton;
    @FXML private Button spinButton;
    @FXML private ImageView spinner;
    @FXML private Label yourNumberLabel;

    private final PlayerProfile profile = CasinoSession.getProfile();
    private int betAmount = 0;
    private final int betStep = 10;

    @FXML
    private void BackToMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vinnsla/SafeCasino.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SafeCasino");
            stage.show();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to return to menu.", e);
        }
    }


    @FXML
    private void handleStartPressed(ActionEvent event) {
        // Show gameplay UI
        balanceLabel.setVisible(true);
        betLabel.setVisible(true);
        resultLabel.setVisible(true);
        betNumberField.setVisible(true);
        increaseBetButton.setVisible(true);
        decreaseBetButton.setVisible(true);
        backButton.setVisible(true);
        yourNumberLabel.setVisible(true);

        // Swap start button for spin button
        startButton.setVisible(false);
        spinButton.setVisible(true);

        // Animate spinner (optional visual effect on entry)
        RotateTransition rotate = new RotateTransition(Duration.seconds(1), spinner);
        rotate.setByAngle(360);
        rotate.setInterpolator(Interpolator.EASE_OUT);
        rotate.play();
    }

    private void updateBetDisplay() {
        if (profile != null) {
            balanceLabel.setText("Balance: $" + profile.getBalance());
        }
        betLabel.setText("Bet: $" + betAmount);
        checkIfBroke();
    }

    @FXML
    private void increaseBet(ActionEvent event) {
        if (profile != null && (betAmount + betStep) <= profile.getBalance()) {
            betAmount += betStep;
            updateBetDisplay();
        }
    }

    @FXML
    private void decreaseBet(ActionEvent event) {
        if (betAmount >= betStep) {
            betAmount -= betStep;
            updateBetDisplay();
        }
    }

    @FXML
    private void startRoulette(ActionEvent event) {
        if (betAmount == 0) {
            resultLabel.setText("Please place a bet.");
            return;
        }

        int chosenNumber;
        try {
            chosenNumber = Integer.parseInt(betNumberField.getText().trim());
            if (chosenNumber < 0 || chosenNumber > 36) {
                resultLabel.setText("Choose a number between 0 and 36.");
                return;
            }
        } catch (NumberFormatException e) {
            resultLabel.setText("Invalid number.");
            return;
        }

        profile.decreaseBalance(betAmount);
        int currentBet = betAmount;
        betAmount = 0;
        updateBetDisplay();

        resultLabel.setText("Spinning...");

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "Spin interrupted.", e);
            }

            int result = (int) (Math.random() * 37);
            boolean win = result == chosenNumber;
            int winnings = win ? currentBet * 36 : 0;

            if (win) {
                profile.increaseBalance(winnings);
            }

            Platform.runLater(() -> {
                resultLabel.setText("Ball landed on " + result + (win ? " — YOU WIN $" + winnings + "!" : " — You lost."));
                updateBetDisplay();
            });
        }).start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateBetDisplay();

        // Hide controls until start is pressed
        balanceLabel.setVisible(false);
        betLabel.setVisible(false);
        resultLabel.setVisible(false);
        betNumberField.setVisible(false);
        increaseBetButton.setVisible(false);
        decreaseBetButton.setVisible(false);
        backButton.setVisible(false);
        yourNumberLabel.setVisible(false);
        spinButton.setVisible(false);
    }

    private void checkIfBroke() {
        if (profile.getBalance() <= 0) {
            showBankruptImage();
        }
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
}
