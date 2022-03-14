package Entities;

import java.util.*;

public class Game {
    private List<Player> players = new ArrayList<>();
    private Deck deck;
    private Pot pot = new Pot();
    private float prevBet =0;

    public List<Player> getPlayers() {
        return players;
    }

    public void dealPreFlop(){
        Iterator<Card> it = deck.getDeck().iterator();
        int i=0;
        int j=0;
        Card card = new Card();
        while (it.hasNext() && i<(players.size()*2) ){
            card=it.next();
            players.get(j).getHand().getHandCards().add(card);
            it.remove();
            i++;
            j++;
            if ( j == players.size())
                j=0;

        }
    }

    public void burnCard(){
        deck.getDeck().remove(0);
    }

    public void dealFlop(){
        burnCard();
        Iterator<Card> it = deck.getDeck().iterator();
        int i=0;
        Card card = new Card();

        while (it.hasNext() && i<3) {
            card= it.next();
            for (Player player : players)
                player.getHand().getHandCards().add(card);
            it.remove();
            i++;
        }
    }

    public void dealTurn(){
        burnCard();
        players.stream().forEach(p->p.getHand().getHandCards().add(deck.getDeck().get(0)));
        deck.getDeck().remove(0);
    }

    public void dealRiver(){
        burnCard();
        players.stream().forEach(p->p.getHand().getHandCards().add(deck.getDeck().get(0)));
        deck.getDeck().remove(0);
    }

    public void run(){ // Hands evaluation test
        deck = new Deck();
        System.out.println("----------Preflop");

        dealPreFlop();
        players.forEach(p->{
            System.out.print("player "+p.getId()+":\t");
            System.out.print(p.getHand().evaluateHand()+"\t");
            System.out.println(p.getHand().getHandCards()+ "\tScore:"+p.getHand().getScore());
        });
        System.out.println("");

        System.out.println("----------Flop");
        dealFlop();
        players.forEach(p->{
            System.out.print("player "+p.getId()+":\t");
            System.out.print(p.getHand().evaluateHand()+"\t");
            System.out.println(p.getHand().getHandCards()+ "\tScore:"+p.getHand().getScore());
        });
        System.out.println("");

        System.out.println("----------Turn");
        dealTurn();
        players.forEach(p->{
            System.out.print("player "+p.getId()+":\t");
            System.out.print(p.getHand().evaluateHand()+"\t");
            System.out.println(p.getHand().getHandCards()+ "\tScore:"+p.getHand().getScore());
        });
        System.out.println("");

        System.out.println("----------River");
        dealRiver();
        players.forEach(p->{
            System.out.print("player "+p.getId()+":\t");
            System.out.print(p.getHand().evaluateHand()+"\t");
            System.out.println(p.getHand().getHandCards()+ "\tScore:"+p.getHand().getScore());
        });
        System.out.println("");


        decideWinningHand();
    }

    public List<Player> decideWinningHand(){ // To be removed (implemented in Pot)
        double minHandScore =-1;
        List<Player> winningPlayersList = new ArrayList<>();
        Player qualifiedPlayer = new Player();

        for (Player player : players){
            if(!player.isHasFolded()){
                if (player.getHand().getScore()>minHandScore){
                    minHandScore = player.getHand().getScore();
                    qualifiedPlayer=player;
                    winningPlayersList.clear();
                    winningPlayersList.add(player);
                }
                else if (player.getHand().getScore()==minHandScore){ //  then compare Kickers
                    if (player.getHand().compareTo(qualifiedPlayer.getHand())>0){ // if Kicker is bigger
                        qualifiedPlayer=player;
                        winningPlayersList.clear();
                        winningPlayersList.add(player);
                    }
                    else if (player.getHand().compareTo(qualifiedPlayer.getHand())==0)  // else if same Kickers
                        winningPlayersList.add(player);
                }
            }

        }
        System.out.println("Winners are ---->>"+winningPlayersList);
        return winningPlayersList;
    }

    public void play(){
        players.forEach(p-> pot.getPlayers().add(p)); // adding players to the POT playersList
        int activePlayers = players.size(); //  (!hasFolded  && !isAllin) > 1
        deck = new Deck(); // init Deck
        pot.setPotSize(0); //init Pot Size
        int choice; // Menu choice
        int i; // Player counter
        Scanner input = new Scanner(System.in);
        int j=0; // Round counter

        while(j<4 && players.stream().filter(player -> !player.isHasFolded()).count()>1) { // At least 2 players are committed
            prevBet=0;
            switch (j){
                case 0:{
                    System.out.println("----------PreFlop------------ Pot :"+pot.getPotSize());
                    dealPreFlop();
                    break;
                }
                case 1: {
                    System.out.println("----------Flop------------ Pot :"+pot.getPotSize());
                    dealFlop();
                    break;
                }
                case 2: {
                    System.out.println("----------Turn------------ Pot :"+pot.getPotSize());
                    dealTurn();
                    break;
                }
                case 3: {
                    System.out.println("----------River ------------ Pot :"+pot.getPotSize());
                    dealRiver();
                    break;
                }
            }

            if(activePlayers>1){ // (!hasFolded  || !isAllin) > 1
                i=0;
                while(i < players.size() && !players.get(i).isHasPlayed()) { // if Player[i] hasn't made a move
                    choice = 0;
                    if (!players.get(i).isHasFolded()) {
                        System.out.print(players.get(i) + "\t\t");
                        System.out.println(players.get(i).getHand().getHandCards());
                        if (!players.get(i).isAllIn()) {
                            while ((choice < 1 || choice > 5)) {
                                if (prevBet == 0)
                                    System.out.print("(1)Check\t\t");
                                else
                                    System.out.print("(2)Call :" + (prevBet - players.get(i).getCurrentBet()) + "\t\t");
                                System.out.print("(3)Bet/Raise\t\t");
                                System.out.print("(4)Fold\t\t");
                                System.out.print("Choice :\t\t:");
                                choice = input.nextInt();
                            }
                            switch (choice) {
                                case 1: { // CHECK
                                    players.get(i).check();
                                    break;
                                }
                                case 2: { // CALL
                                    players.get(i).call(prevBet);
                                    if (players.get(i).isAllIn()) {
                                        activePlayers--;
                                    }
                                    break;
                                }
                                case 3: { // BET / RAISE
                                    prevBet = players.get(i).raise(prevBet, players);
                                    if (players.get(i).isAllIn())
                                        activePlayers--;
                                    break;
                                }
                                case 4: { // FOLD
                                    players.get(i).fold();
                                    activePlayers--;
                                    break;
                                }
                            }
                        }
                    }
                    System.out.println("");
                    i++;
                    if (i == players.size() && activePlayers > 1) // if there's a BET/RAISE
                        i = 0;
                }// All Players have made their move
            }

            for(Player player: players){ // Preparing for the next CARD
                player.setHasPlayed(false);
                player.setPotContribution(player.getPotContribution()+ player.getCurrentBet());
                pot.setPotSize(pot.getPotSize()+player.getCurrentBet());
                player.setCurrentBet(0);
            }
            j++; // Deal next Round
        } // Hand is Over

        for(Player p : players){ // Evaluating Players Hands
            if (!p.isHasFolded()){
                System.out.print(p+"\t\t");
                System.out.println(p.getHand().getHandCards());
                System.out.println("player "+p.getId()+"\t"+p.getHand().evaluateHand()+ "\tScore :"+p.getHand().getScore());
            }
        }

        pot.distributeChips(); // Distribute Chips
        System.out.println(players);

        for (Player player : players){
            player.setPotContribution(0);
        }
        // Ready for the next Hand
    }
}
