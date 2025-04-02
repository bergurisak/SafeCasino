package vinnsla;

public class Card {
    String value;
    String type;

    public Card(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public String toString() {
        return value + "-" + type;
    }

    public int getValue() {
        if ("JQK".contains(value)) return 10;
        if (value.equals("A")) return 11;
        return Integer.parseInt(value);
    }

    public boolean isAce() {
        return value.equals("A");
    }

    public String getImagePath() {
        return "/cards/" + this + ".png";
    }
}
