package Entities;

import java.util.*;

public class Pot {
    private float potSize;

    public Pot() {
        potSize=0;
    }

    public float getPotSize() {
        return potSize;
    }

    public void setPotSize(float potSize) {
        this.potSize = potSize;
    }

    public List<Player> getWinners(List<Player> players, int uncheckedPlayerIndex) {
        // Players Hands must be evaluated

        double minHandScore =-1;
        List<Player> winningPlayersList = new ArrayList<>();
        Player qualifiedPlayer = new Player();

        for (Player player : players.subList(uncheckedPlayerIndex,players.size())){ // previous players are already checked
            if(!player.hasFolded()){
                if (player.getHand().getScore() > minHandScore){ // if has a better handScore
                    minHandScore = player.getHand().getScore();
                    qualifiedPlayer=player;
                    winningPlayersList.clear();
                    winningPlayersList.add(player);
                }
                else if (player.getHand().getScore() == minHandScore){ //  then compare Kickers
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
        //System.out.println("Winners are ---->>"+winningPlayersList);
        return winningPlayersList;

    }
    public void sortPlayersByPotContributionASC(List <Player> players){
        players.sort((p1, p2) -> {
            if (p1.getPotContribution() > p2.getPotContribution())
                return 1;
            else if (p1.getPotContribution() < p2.getPotContribution())
                return -1;
            return 0;
        });
    }

    public void evaluatePlayersHands(List <Player> players){
        for(Player p : players){ // Evaluating Players Hands
            if (!p.hasFolded()){
                System.out.println(p+"\nHand Strength :" + p.getHand().evaluateHand()+ "\tScore :"+p.getHand().getScore()+"\n");
            }
        }
    }

    public void distributeChips(List <Player> players){
        evaluatePlayersHands(players);
        sortPlayersByPotContributionASC(players);

        List<Player> potWinners;
        float sidePot;
        float lastWinnerContrib = 0;
        float lostContribs = 0; // contributions from folds and loses
        Player player ;
        int i=0;

        while ( i<players.size() ){
            player = players.get(i);

            if (potSize>0){
                potWinners = getWinners(players,i);
                if (potWinners.contains(player)) { // if is a winner
                    if (potWinners.size() == 1) {
                        sidePot = (lostContribs + (player.getPotContribution() - lastWinnerContrib) * (players.size()-i)); // uncheckedPlayers = players.size()-i
                        lostContribs = 0;
                        lastWinnerContrib = player.getPotContribution();
                    } else {
                        sidePot = ((lostContribs + (player.getPotContribution() - lastWinnerContrib) * (players.size()-i)) / potWinners.size());
                        lostContribs /= potWinners.size();
                    }
                    player.addChips(sidePot);
                    potSize -= sidePot;
                }
                else lostContribs += player.getPotContribution() - lastWinnerContrib;
            }

            player.initForNextHand();
            i++;
        }
    }


    @Override
    public String toString() {
        return "Pot Size ="+potSize;
    }

}
