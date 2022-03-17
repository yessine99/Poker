package Entities;

import java.util.Objects;

public class Card implements Comparable<Card> {
    private int value;
    private String suit;

    public Card(int value, String suit) {
        this.value = value;
        this.suit = suit;
    }

    public Card() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return getValue()==card.getValue() && getSuit().equals(card.getSuit());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue(), getSuit().hashCode());
    }

    public int getValue() {
        return value;
    }

    public String getSuit() {
        return suit;
    }


    @Override
    public int compareTo(Card otherCard) {
        return this.value-otherCard.getValue();
    }

    @Override
    public String toString() {
        return switch (value) {
            case 11 -> "J of " + suit;
            case 12 -> "Q of " + suit;
            case 13 -> "K of " + suit;
            case 14 -> "A of " + suit;
            default -> value + " of " + suit;
        };
    }
}
