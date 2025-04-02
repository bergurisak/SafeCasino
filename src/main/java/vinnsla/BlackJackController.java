package vinnsla;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;

public class BlackJackController implements Initializable {
    private PlayerProfile profile;

    public void setProfile(PlayerProfile profile) {
        this.profile = profile;
        updateBetDisplay();
    }

    @FXML private Text balanceText;
    @FXML private Text betText;
    @FXML private AnchorPane gamePane;
    @FXML private Button hitButton;
    @FXML private Button stayButton;
    @FXML private Text resultText;

    private final int CARD_WIDTH = 110;
    private final int CARD_HEIGHT = 154;

    private ArrayList<Card> deck;
    private Random random = new Random();

    private Card hiddenCard;
    private ArrayList<Card> dealerHand = new ArrayList<>();
    private int dealerSum;
    private int dealerAceCount;

    private ArrayList<Card> playerHand = new ArrayList<>();
    private int playerSum;
    private int playerAceCount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hitButton.setDisable(true);
        stayButton.setDisable(true);
        updateBetDisplay();
    }

    private void startGame() {
        gamePane.getChildren().clear();
        buildDeck();
        shuffleDeck();

        dealerHand.clear();
        playerHand.clear();
        dealerSum = playerSum = dealerAceCount = playerAceCount = 0;
        resultText.setText("");

        hiddenCard = drawCard(true);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card visibleDealerCard = drawCard(false);
        dealerSum += visibleDealerCard.getValue();
        dealerAceCount += visibleDealerCard.isAce() ? 1 : 0;
        dealerHand.add(visibleDealerCard);

        for (int i = 0; i < 2; i++) {
            Card card = drawCard(false);
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

    private Card drawCard(boolean hidden) {
        Card card = deck.remove(deck.size() - 1);

        Image image;
        if (hidden) {
            image = new Image(getClass().getResourceAsStream("/cards/BACK.png"));
        } else {
            image = new Image(getClass().getResourceAsStream(card.getImagePath()));
        }

        ImageView cardView = new ImageView(image);
        cardView.setFitWidth(CARD_WIDTH);
        cardView.setFitHeight(CARD_HEIGHT);

        int y = (hiddenCard == null) ? 50 : 300;
        int x = (hiddenCard == null)
                ? 20 + dealerHand.size() * (CARD_WIDTH + 5)
                : 20 + playerHand.size() * (CARD_WIDTH + 5);

        cardView.setLayoutX(x);
        cardView.setLayoutY(y);
        gamePane.getChildren().add(cardView);

        return card;
    }

    @FXML
    private void onHit() {
        Card card = drawCard(false);
        playerSum += card.getValue();
        playerAceCount += card.isAce() ? 1 : 0;
        playerHand.add(card);

        if (reducePlayerAce() > 21) {
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
        if (hiddenCard == null) {
            resultText.setText("Error: Game not started. Click Deal.");
            return;
        }

        // Replace hidden card with the actual one
        ImageView hiddenImg = new ImageView(new Image(getClass().getResourceAsStream(hiddenCard.getImagePath())));
        hiddenImg.setFitWidth(CARD_WIDTH);
        hiddenImg.setFitHeight(CARD_HEIGHT);
        hiddenImg.setLayoutX(20);
        hiddenImg.setLayoutY(50);
        gamePane.getChildren().add(hiddenImg);

        while (dealerSum < 17) {
            Card card = drawCard(false);
            dealerSum += card.getValue();
            dealerAceCount += card.isAce() ? 1 : 0;
            dealerHand.add(card);
        }

        dealerSum = reduceDealerAce();
        playerSum = reducePlayerAce();

        String message;
        if (playerSum > 21) {
            message = "You Lose!";
            profile.loseBet();
        } else if (dealerSum > 21 || playerSum > dealerSum) {
            message = "You Win!";
            profile.winBet();
        } else if (playerSum == dealerSum) {
            message = "Tie!";
            profile.tieBet();
        } else {
            message = "You Lose!";
            profile.loseBet();
        }

        resultText.setText(message);
        updateBetDisplay();
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
            balanceText.setText("Balance: $" + profile.getBalance());
            betText.setText("Bet: $" + profile.getCurrentBet());
        }
    }

    @FXML
    private void increaseBet() {
        profile.increaseBet(10);
        updateBetDisplay();
    }

    @FXML
    private void decreaseBet() {
        profile.decreaseBet(10);
        updateBetDisplay();
    }

    @FXML
    private void deal() {
        if (profile.getCurrentBet() > 0) {
            startGame();
        } else {
            resultText.setText("Place a bet first!");
        }
    }
}
