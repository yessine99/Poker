package Entities;

import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        //specific Hand testing
        /*Deck deck = new Deck();
        Hand hand = new Hand();
        hand.getHandCards().add(new Card(8,CardSuit.Spades.toString()));
        hand.getHandCards().add(new Card(10,CardSuit.Spades.toString()));
        hand.getHandCards().add(new Card(7,CardSuit.Spades.toString()));

        hand.getHandCards().add(new Card(9,CardSuit.Spades.toString()));
        hand.getHandCards().add(new Card(11,CardSuit.Spades.toString()));
        hand.getHandCards().add(new Card(5,CardSuit.Hearts.toString()));
        hand.getHandCards().add(new Card(13,CardSuit.Hearts.toString()));
        hand.getHandCards().add(new Card(14,CardSuit.Hearts.toString()));


        System.out.println("Hand ="+hand.getHandCards());
        Collections.sort(hand.getHandCards(),Collections.reverseOrder());
        System.out.println("Sorted Hand ="+hand.getHandCards());
        System.out.println(hand.evaluateHand());
        System.out.println(hand.getScore());
*/
        Game game = new Game();

        Player p1 = new Player(1);
        Player p2 = new Player(2);
        Player p3 = new Player(3);
        //Player p4 = new Player(4);
        game.getPlayers().add(p1);
        game.getPlayers().add(p2);
        game.getPlayers().add(p3);
        //game.getPlayers().add(p4);


        //game.run(); // hand evaluator
        game.play(); //  hand simulation

    }
}
