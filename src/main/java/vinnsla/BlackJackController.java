package vinnsla;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class BlackJackController implements Initializable {
    private PlayerProfile profile;
    private boolean gameInProgress = false;

    public void setProfile(PlayerProfile profile) {
        this.profile = profile;
        System.out.println("Profile set: $" + profile.getBalance());
        updateBetDisplay();

    }

    @FXML private Text balanceText;
    @FXML private Text betText;
    @FXML private AnchorPane gamePane;
    @FXML private Button hitButton;
    @FXML private Button stayButton;
    @FXML private Button dealButton;
    @FXML private Text resultText;

    private final int CARD_WIDTH = 110;
    private final int CARD_HEIGHT = 154;

    private ArrayList<Card> deck;
    private final Random random = new Random();

    private Card hiddenCard;
    private ImageView hiddenCardBackImageView;
    private final ArrayList<Card> dealerHand = new ArrayList<>();
    private int dealerSum;
    private int dealerAceCount;

    private final ArrayList<Card> playerHand = new ArrayList<>();
    private final ArrayList<ImageView> playerCardViews = new ArrayList<>();
    private int playerSum;
    private int playerAceCount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hitButton.setDisable(true);
        stayButton.setDisable(true);
        updateBetDisplay();
    }

    @FXML
    private void deal() {
        if (gameInProgress) {
            showBigMessage("Play the game lil bro");
            return;
        }
        if (profile != null && profile.getCurrentBet() > 0) {
            startGame();
        } else {
            resultText.setText("Place a bet before dealing.");
        }
    }

    private void showBigMessage(String msg) {
        Text popup = new Text(msg);
        popup.setFont(Font.font("Arial", 40));
        popup.setFill(Color.RED);
        popup.setOpacity(0);
        popup.setLayoutX(300);
        popup.setLayoutY(250);

        gamePane.getChildren().add(popup);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), popup);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), popup);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(2));

        fadeOut.setOnFinished(e -> gamePane.getChildren().remove(popup));

        new SequentialTransition(fadeIn, fadeOut).play();
    }

    private void startGame() {
        gameInProgress = true;
        gamePane.getChildren().removeIf(node -> node instanceof ImageView || node instanceof Circle);

        buildDeck();
        shuffleDeck();

        dealerHand.clear();
        playerHand.clear();
        playerCardViews.clear();
        dealerSum = playerSum = dealerAceCount = playerAceCount = 0;
        resultText.setText("");

        hiddenCard = deck.remove(deck.size() - 1);
        InputStream backStream = getClass().getResourceAsStream("/cards/BACK.png");
        if (backStream == null) return;
        Image backImg = new Image(backStream);
        hiddenCardBackImageView = new ImageView(backImg);
        hiddenCardBackImageView.setFitWidth(CARD_WIDTH);
        hiddenCardBackImageView.setFitHeight(CARD_HEIGHT);
        hiddenCardBackImageView.setLayoutX(20);
        hiddenCardBackImageView.setLayoutY(50);
        gamePane.getChildren().add(hiddenCardBackImageView);

        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card visibleDealerCard = drawCard(false, true);
        dealerSum += visibleDealerCard.getValue();
        dealerAceCount += visibleDealerCard.isAce() ? 1 : 0;
        dealerHand.add(visibleDealerCard);

        for (int i = 0; i < 2; i++) {
            Card card = drawCard(false, false);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }

        hitButton.setDisable(false);
        stayButton.setDisable(false);
    }

    private void buildDeck() {
        deck = new ArrayList<>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (String type : types) {
            for (String value : values) {
                deck.add(new Card(value, type));
            }
        }
    }

    private void shuffleDeck() {
        Collections.shuffle(deck, random);
    }

    private Card drawCard(boolean hidden, boolean isDealer) {
        if (deck.isEmpty()) {
            resultText.setText("Error: Deck is empty.");
            return null;
        }

        Card card = deck.remove(deck.size() - 1);
        InputStream stream = getClass().getResourceAsStream(hidden ? "/cards/BACK.png" : card.getImagePath());
        if (stream == null) return null;

        ImageView cardView = new ImageView(new Image(stream));
        cardView.setFitWidth(CARD_WIDTH);
        cardView.setFitHeight(CARD_HEIGHT);

        int y = isDealer ? 50 : 350;
        int offset = isDealer ? dealerHand.size() + 1 : playerHand.size();
        int x = 20 + offset * (CARD_WIDTH + 5);

        cardView.setLayoutX(x);
        cardView.setLayoutY(y);
        gamePane.getChildren().add(cardView);

        if (!isDealer) playerCardViews.add(cardView);

        return card;
    }

    @FXML
    private void onHit() {
        Card card = drawCard(false, false);
        if (card == null) return;

        playerSum += card.getValue();
        playerAceCount += card.isAce() ? 1 : 0;
        playerHand.add(card);

        if (reducePlayerAce() > 21) {
            highlightBustedHand();
            hitButton.setDisable(true);
            stayButton.setDisable(true);
            revealDealerCards();
        }
    }

    @FXML
    private void onStay() {
        hitButton.setDisable(true);
        stayButton.setDisable(true);
        revealDealerCards();
    }

    private void revealDealerCards() {
        if (hiddenCard == null || hiddenCardBackImageView == null) {
            resultText.setText("Error: Game not started. Click Deal.");
            return;
        }

        ScaleTransition flipOut = new ScaleTransition(Duration.millis(150), hiddenCardBackImageView);
        flipOut.setFromX(1);
        flipOut.setToX(0);

        flipOut.setOnFinished(e -> {
            InputStream stream = getClass().getResourceAsStream(hiddenCard.getImagePath());
            if (stream == null) return;
            hiddenCardBackImageView.setImage(new Image(stream));

            ScaleTransition flipIn = new ScaleTransition(Duration.millis(150), hiddenCardBackImageView);
            flipIn.setFromX(0);
            flipIn.setToX(1);
            flipIn.play();

            flipIn.setOnFinished(event -> {
                dealerHand.add(hiddenCard);

                while (dealerSum < 17) {
                    Card card = drawCard(false, true);
                    if (card == null) return;
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1 : 0;
                    dealerSum = reduceDealerAce();
                    dealerHand.add(card);
                }

                dealerSum = reduceDealerAce();
                playerSum = reducePlayerAce();

                String message;
                if (playerSum > 21) {
                    message = "You Lose!";
                    playSound("bust.mp3");
                    highlightBustedHand();
                    profile.loseBet();
                } else if (dealerSum > 21 || playerSum > dealerSum) {
                    message = "You Win!";
                    playSound("win.mp3");
                    profile.winBet();
                    launchConfetti();
                } else if (playerSum == dealerSum) {
                    message = "Tie!";
                    playSound("tie.mp3");
                    profile.tieBet();
                } else {
                    message = "You Lose!";
                    playSound("lose.mp3");
                    profile.loseBet();
                }

                resultText.setText(message);
                animateResult();
                updateBetDisplay();
                gameInProgress = false;

                if (profile.getBalance() > 1000) showCashIsKing();
            });
        });

        flipOut.play();
    }

    private void showCashIsKing() {
        InputStream stream = getClass().getResourceAsStream("/images/cashisking.jpg");
        if (stream == null) return;
        ImageView view = new ImageView(new Image(stream));
        view.setFitWidth(400);
        view.setPreserveRatio(true);
        view.setOpacity(0);
        view.setLayoutX(300);
        view.setLayoutY(200);
        gamePane.getChildren().add(view);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), view);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), view);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(2));
        fadeOut.setOnFinished(e -> gamePane.getChildren().remove(view));

        new SequentialTransition(fadeIn, fadeOut).play();
    }

    private void launchConfetti() {
        for (int i = 0; i < 30; i++) {
            Circle circle = new Circle(5, Color.hsb(Math.random() * 360, 1, 1));
            circle.setLayoutX(500);
            circle.setLayoutY(100);
            gamePane.getChildren().add(circle);

            double dx = Math.random() * 400 - 200;
            double dy = 300 + Math.random() * 100;

            Timeline drop = new Timeline(
                    new KeyFrame(Duration.ZERO),
                    new KeyFrame(Duration.seconds(1.5), new KeyValue(circle.translateXProperty(), dx),
                            new KeyValue(circle.translateYProperty(), dy))
            );
            drop.setOnFinished(evt -> gamePane.getChildren().remove(circle));
            drop.play();
        }
    }

    private void highlightBustedHand() {
        for (ImageView img : playerCardViews) {
            img.setStyle("-fx-effect: dropshadow(gaussian, red, 15, 0, 0, 0);");
        }
    }

    private void animateResult() {
        ScaleTransition bounce = new ScaleTransition(Duration.millis(300), resultText);
        bounce.setFromX(1);
        bounce.setToX(1.3);
        bounce.setCycleCount(2);
        bounce.setAutoReverse(true);
        bounce.play();
    }

    private void playSound(String fileName) {
        URL soundUrl = getClass().getResource("/mp3/" + fileName);
        if (soundUrl == null) {
            System.out.println("Sound file not found: " + fileName);
            return;
        }
        AudioClip sound = new AudioClip(soundUrl.toExternalForm());
        sound.play();
    }

    private int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount--;
        }
        return playerSum;
    }

    private int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount--;
        }
        return dealerSum;
    }

    private void updateBetDisplay() {
        if (profile != null) {
            balanceText.setLayoutX(700);
            betText.setLayoutX(700);
            balanceText.setText("Balance: $" + profile.getBalance());
            betText.setText("Bet: $" + profile.getCurrentBet());
        }
    }

    @FXML
    private void increaseBet() {
        if (profile != null) {
            profile.increaseBet(10);
            updateBetDisplay();
        }
    }

    @FXML
    private void decreaseBet() {
        if (profile != null) {
            profile.decreaseBet(10);
            updateBetDisplay();
        }
    }
}