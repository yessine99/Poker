package Entities;

import Enums.CardSuit;
import Enums.CardValue;

import java.util.*;

public class Deck {
    private final List<Card> deck = new ArrayList<>();

    public  Deck(){
    }

    public void construct (){
        deck.clear();
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
