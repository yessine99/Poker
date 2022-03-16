package Entities;

import java.util.*;

public class Game {
    private List<Player> players = new ArrayList<>();
    private Deck deck = new Deck();
    private Pot pot = new Pot();
    private  Table table = new Table();
    private float prevBet =0;
    private int activePlayers; // (!hasFolded &&  !isAllin)
    private int bigBlindIndex;
    private int smallBlindIndex;

    public List<Player> getPlayers() {
        return players;
    }

    private void dealPreFlop(){
        Iterator<Card> it = deck.getDeck().iterator();
        int i=0;
        int j=0;
        Card card = new Card();
        while (it.hasNext() && i<(players.size()*2) ){
            card=it.next();
            players.get(j).getHand().getCards().add(card);
            it.remove();
            i++;
            j++;
            if ( j == players.size())
                j=0;

        }
    }

    private void burnCard(){
        deck.getDeck().remove(0);
    }

    private void dealFlop(){
        burnCard();
        Iterator<Card> it = deck.getDeck().iterator();
        int i=0;
        Card card = new Card();

        while (it.hasNext() && i<3) {
            card= it.next();
            for (Player player : players)
                player.getHand().getCards().add(card);
            it.remove();
            i++;
        }
    }

    private void dealTurnOrRiver(){
        burnCard();
        players.stream().forEach(p->p.getHand().getCards().add(deck.getDeck().get(0)));
        deck.getDeck().remove(0);
    }

    private void dealRound(int round){
        switch (round){
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
                dealTurnOrRiver();
                break;
            }
            case 3: {
                System.out.println("----------River ------------ Pot :"+pot.getPotSize());
                dealTurnOrRiver();
                break;
            }
        }
    }

    private void startBettingRound(int i ){
        Scanner input = new Scanner(System.in);
        int choice;

        while ( i< players.size() && ( players.get(i).getCurrentBet() != prevBet || !players.get(i).hasPlayed()) ){
            choice =0;
            if (!players.get(i).hasFolded() && !players.get(i).isAllIn()) {
                System.out.println(players.get(i));
                while ((choice < 1 || choice > 5)) {
                    if (players.get(i).getCurrentBet() == prevBet )
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
            System.out.println("");
            i++;
            if (i == players.size()) // if there's a BET/RAISE
                i = 0;
        }
    }

    public void initHand(){
        deck.construct();
        pot.setPotSize(0);
        activePlayers = players.size();
        bigBlindIndex = 0;
        if(bigBlindIndex==0)
            smallBlindIndex = players.size()-1;
        else
            smallBlindIndex = bigBlindIndex - 1;

        System.out.println("Blinds Are : "+table.getBlinds()+ " / "+table.getBlinds()/2);
        players.get(bigBlindIndex).setBlind(table.getBlinds());
        System.out.println("Player "+players.get(bigBlindIndex).getId() + " is big blind");
        players.get(smallBlindIndex).setBlind(table.getBlinds()/2);
        System.out.println("Player "+players.get(smallBlindIndex).getId() + " is small blind");
        prevBet = table.getBlinds();
    }

    public void play(){
        initHand();
        int i; // Player counter
        int j=0; // Round counter

        if (bigBlindIndex == players.size() - 1)
            i = 0;
        else
            i = bigBlindIndex + 1;

        while(j<4 && players.stream().filter(player -> !player.hasFolded()).count()>1) { // At least 2 players are committed
            dealRound(j);
            if(activePlayers >1 ) // (!hasFolded  && !isAllin) > 1
                startBettingRound(i);

            for(Player player: players){ // Preparing for the next CARD
                pot.setPotSize(pot.getPotSize()+player.getCurrentBet());
                player.initForNextRound();
            }
            i = smallBlindIndex;
            prevBet = 0;
            j++;
        }

        pot.distributeChips(players); // Distribute chips
        System.out.println("\nPlayers after chips distribution :\n"+players);

    }


    public void handEvaluationSimulation(){ // Hands evaluation test
        deck.construct();
        for (int j=0 ; j<4 ; j++){
            switch (j){
                case 0:{
                    System.out.println("\n----------PreFlop------------ ");
                    dealPreFlop();
                    break;
                }
                case 1: {
                    System.out.println("\n----------Flop------------");
                    dealFlop();
                    break;
                }
                case 2: {
                    System.out.println("\n----------Turn------------");
                    dealTurnOrRiver();
                    break;
                }
                case 3: {
                    System.out.println("\n----------River ------------");
                    dealTurnOrRiver();
                    break;
                }
            }
            players.forEach(p->{
                System.out.print("player "+p.getId()+":\t");
                System.out.print(p.getHand().evaluateHand()+"\t");
                System.out.println(p.getHand().getCards()+ "\tScore:"+p.getHand().getScore());
            });
        }
        System.out.println("\nWinners are --->> :"+pot.getWinners(players,0));
    }
}
