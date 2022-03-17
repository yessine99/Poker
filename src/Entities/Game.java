package Entities;

import java.util.*;

public class Game {
    private final List<Player> players = new ArrayList<>();
    private final Deck deck = new Deck();
    private final Pot pot = new Pot();
    private final Table table = new Table();
    private float prevBet =0;
    private int allinPlayers;
    private int foldedPlayers;
    private int bigBlindIndex;
    private int smallBlindIndex;

    public List<Player> getPlayers() {
        return players;
    }

    private void dealPreFlop(){
        Iterator<Card> it = deck.getDeck().iterator();
        int i=0;
        int j=0;
        Card card;
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
        Card card;

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
        players.forEach(p->p.getHand().getCards().add(deck.getDeck().get(0)));
        deck.getDeck().remove(0);
    }

    private void dealRound(int round){
        switch (round) {
            case 0 -> {
                System.out.println("----------PreFlop------------ Pot :" + pot.getPotSize());
                dealPreFlop();
            }
            case 1 -> {
                System.out.println("----------Flop------------ Pot :" + pot.getPotSize());
                dealFlop();
            }
            case 2 -> {
                System.out.println("----------Turn------------ Pot :" + pot.getPotSize());
                dealTurnOrRiver();
            }
            case 3 -> {
                System.out.println("----------River ------------ Pot :" + pot.getPotSize());
                dealTurnOrRiver();
            }
        }
    }

    private void startBettingRound(int i ){
        Scanner input = new Scanner(System.in);
        int choice;

        while ( i < players.size() && foldedPlayers < players.size()-1 && ( players.get(i).getCurrentBet() != prevBet || !players.get(i).hasPlayed() ) ){
            choice =0;
            if ( !players.get(i).hasFolded() && !players.get(i).isAllIn() ) {
                System.out.println(players.get(i));
                while ((choice < 1 || choice > 5)) {
                    if (players.get(i).getCurrentBet() == prevBet )
                        System.out.print("(1)Check\t\t");
                    else
                        System.out.print("(2)Call :" + ((prevBet - players.get(i).getCurrentBet())<players.get(i).getChips() ? (prevBet - players.get(i).getCurrentBet()) : players.get(i).getChips())+ "\t\t");
                    System.out.print("(3)Bet/Raise\t\t");
                    System.out.print("(4)Fold\t\t");
                    System.out.print("Choice :\t\t:");
                    choice = input.nextInt();
                }
                switch (choice) {
                    case 1 -> // CHECK
                            players.get(i).check();
                    case 2 -> { // CALL
                        players.get(i).call(prevBet);
                        if (players.get(i).isAllIn())
                            allinPlayers++;
                    }
                    case 3 -> { // BET / RAISE
                        prevBet = players.get(i).raise(prevBet);
                        if (players.get(i).isAllIn())
                            allinPlayers++;
                    }
                    case 4 -> { // FOLD
                        players.get(i).fold();
                        foldedPlayers++;
                    }
                }
            }
            System.out.println();
            i++;
            if (i == players.size()) // if there's a BET/RAISE
                i = 0;
        }
    }

    public void initHand(){
        deck.construct();
        pot.setPotSize(0);
        foldedPlayers = 0;
        allinPlayers = 0;
        bigBlindIndex = 1;
        if(bigBlindIndex==0)
            smallBlindIndex = players.size()-1;
        else
            smallBlindIndex = bigBlindIndex - 1;

        System.out.println("Blinds Are : "+table.getBlinds()+ " / "+table.getBlinds()/2);
        //BIG BLIND
        players.get(bigBlindIndex).setBlind(table.getBlinds());
        System.out.println("Player "+players.get(bigBlindIndex).getId() + " is big blind");
        //SMALL BLIND
        players.get(smallBlindIndex).setBlind(table.getBlinds()/2);
        System.out.println("Player "+players.get(smallBlindIndex).getId() + " is small blind");

        prevBet = table.getBlinds();
    }

    public void initNextRound(){
        for(Player player: players){
            pot.setPotSize(pot.getPotSize()+player.getCurrentBet());
            player.initForNextRound();
        }
    }

    public void play(){
        initHand();
        int i; // Player counter
        int j=0; // Round counter

        if (bigBlindIndex == players.size() - 1)
            i = 0;
        else i = bigBlindIndex + 1;

        while(j<4 && (players.size() - foldedPlayers > 1) ) { // At least 2 players are committed
            dealRound(j);
            if (players.size() - foldedPlayers - allinPlayers > 1)
                startBettingRound(i);

            initNextRound();
            i = smallBlindIndex;
            prevBet = 0;
            j++;
        }

        pot.distributeChips(players);
        System.out.println("\nPlayers after chips distribution :\n"+players);
    }

    public void handEvaluationSimulation(){ // Hands evaluation test
        deck.construct();
        for (int j=0 ; j<4 ; j++){
            switch (j) {
                case 0 -> {
                    System.out.println("\n----------PreFlop------------ ");
                    dealPreFlop();
                }
                case 1 -> {
                    System.out.println("\n----------Flop------------");
                    dealFlop();
                }
                case 2 -> {
                    System.out.println("\n----------Turn------------");
                    dealTurnOrRiver();
                }
                case 3 -> {
                    System.out.println("\n----------River ------------");
                    dealTurnOrRiver();
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
