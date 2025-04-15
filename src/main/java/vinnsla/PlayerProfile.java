package vinnsla;

public class PlayerProfile {
    private int balance;
    private int currentBet;

    public PlayerProfile(int initialBalance) {
        this.balance = initialBalance;
        this.currentBet = 0;
    }

    public int getBalance() {
        return balance;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public void increaseBet(int amount) {
        if (amount <= balance) {
            currentBet += amount;
            balance -= amount;
        }
    }

    public void decreaseBet(int amount) {
        if (amount <= currentBet) {
            currentBet -= amount;
            balance += amount;
        }
    }

    public void winBet() {
        balance += currentBet * 2;
        currentBet = 0;
    }

    public void loseBet() {
        currentBet = 0;
    }

    public void tieBet() {
        balance += currentBet;
        currentBet = 0;
    }
    public void increaseBalance(int amount) {
        balance += amount;
    }

    public void decreaseBalance(int amount) {
        if (amount <= balance) {
            balance -= amount;
        }
    }

    public void setBalance(int i) {
    }
}