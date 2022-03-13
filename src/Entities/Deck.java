package Entities;

import java.util.*;

public class Deck {
    private List<Card> deck = new ArrayList<>();

    public  Deck(){
        for (CardSuit suit : CardSuit.values())
            for (CardValue value : CardValue.values())
                deck.add(new Card(value.ordinal()+2,suit.toString()));

        Collections.shuffle(deck);
    }

    public List<Card> getDeck() {
        return deck;
    }

    @Override
    public String toString() {
        return "Deck{" +
                "deck=" + deck +
                '}';
    }
}