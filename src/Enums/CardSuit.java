package Enums;

public enum CardSuit {
    Spades("Spades"),
    Hearts("Hearts"),
    Diamonds("Diamonds"),
    Clubs("Clubs");


    private final String suit;
    CardSuit(final String suit){
        this.suit = suit;
    }

    @Override
    public String toString() {
        return suit;
    }
}
