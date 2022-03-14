package Entities;

import java.util.*;

public class Game {
    private List<Player> players = new ArrayList<>();
    private Deck deck;
    private float pot;
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

    public List<Player> decideWinningHand(){
        double rank =-1;
        List<Player> winningPlayersList = new ArrayList<>();
        Player winningPlayer = new Player();

        for (Player player : players){
            if(!player.isHasFolded()){
                if (player.getHand().getScore()>rank){
                    rank = player.getHand().getScore();
                    winningPlayer=player;
                    winningPlayersList.clear();
                    winningPlayersList.add(player);
                }
                else if (player.getHand().getScore()==rank){
                    if (player.getHand().compareTo(winningPlayer.getHand())>0){
                        winningPlayer=player;
                        winningPlayersList.clear();
                        winningPlayersList.add(player);
                    }
                    else if (player.getHand().compareTo(winningPlayer.getHand())==0)
                        winningPlayersList.add(player);
                }
            }

            }
        System.out.println("Winning Players are "+winningPlayersList);
    return winningPlayersList;
    }

    public void play(){
        int remainingPlayers=players.size();
        deck = new Deck();
        pot=0;
        int choice;
        int i;
        Scanner input = new Scanner(System.in);
        int j=0;
        while(j<4 && players.stream().filter(player -> !player.isHasFolded()).count()>1) {
            prevBet=0;
            switch (j){
                case 0:{
                    System.out.println("----------PreFlop------------ Pot :"+pot);
                    dealPreFlop();
                    break;
                }
                case 1: {
                    System.out.println("----------Flop------------ Pot :"+pot);
                    dealFlop();
                    break;
                }
                case 2: {
                    System.out.println("----------Turn------------ Pot :"+pot);
                    dealTurn();
                    break;
                }
                case 3: {
                    System.out.println("----------River ------------ Pot :"+pot);
                    dealRiver();
                    break;
                }
            }

            i=0;
            if(remainingPlayers>1){
                while(i < players.size() && !players.get(i).isHasPlayed()) {
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
                                case 1: {
                                    players.get(i).check();
                                    break;
                                }
                                case 2: {
                                    players.get(i).call(prevBet);
                                    if (players.get(i).getChips() == 0)
                                        remainingPlayers--;
                                    if (remainingPlayers == 1)
                                        remainingPlayers--;
                                    break;
                                }
                                case 3: {
                                    prevBet = players.get(i).raise(prevBet, players);
                                    if (players.get(i).getChips() == 0)
                                        remainingPlayers--;
                                    break;
                                }
                                case 4: {
                                    players.get(i).fold();
                                    remainingPlayers--;
                                    break;
                                }
                            }
                        }
                    }
                    System.out.println("");
                    i++;
                    if (i == players.size() && remainingPlayers > 1)
                        i = 0;
                }
            }
            j++;
            for(Player player: players){
                player.setHasPlayed(false);
                pot+=player.getCurrentBet();
                player.setCurrentBet(0);
            }
        }
        for(Player p : players){
            if (!p.isHasFolded()){
                System.out.print(p+"\t\t");
                System.out.println(p.getHand().getHandCards());
                System.out.println("player "+p.getId()+"\t"+p.getHand().evaluateHand()+ "\tScore :"+p.getHand().getScore());
            }
        }
        System.out.println("Pot :"+pot);
        List<Player> winningPlayers = new ArrayList<>();
        winningPlayers=decideWinningHand();
        for (Player player : winningPlayers){
            player.setChips(pot/ winningPlayers.size());
            System.out.println(player);
        }



    }
}
