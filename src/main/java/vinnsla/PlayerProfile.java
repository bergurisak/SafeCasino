package vinnsla;

public class PlayerProfile {
    private int balance;
    private int currentBet;

    public PlayerProfile(int startingBalance) {
        this.balance = startingBalance;
        this.currentBet = 0;
    }

    public int getBalance() {
        return balance;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public void increaseBet(int amount) {
        if (balance >= amount) {
            currentBet += amount;
            balance -= amount;
        }
    }

    public void decreaseBet(int amount) {
        if (currentBet >= amount) {
            currentBet -= amount;
            balance += amount;
        }
    }

    public void resetBet() {
        currentBet = 0;
    }

    public void winBet() {
        balance += currentBet * 2;
        resetBet();
    }

    public void tieBet() {
        balance += currentBet;
        resetBet();
    }

    public void loseBet() {
        resetBet();
    }
}
