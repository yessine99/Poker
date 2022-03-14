package Entities;

import java.util.*;

public class Pot {
    private float potSize;
    private List<Player> players = new ArrayList<>();

    public Pot() {
    }

    public float getPotSize() {
        return potSize;
    }

    public void setPotSize(float potSize) {
        this.potSize = potSize;
    }

    public List<Player> getPlayers() {
        return players;
    }




    private List getWinners() {
        // Players Hands must be evaluated

        double minHandScore =-1;
        List<Player> winningPlayersList = new ArrayList<>();
        Player qualifiedPlayer = new Player();

        for (Player player : players){
            if(!player.isHasFolded()){
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


    public void distributeChips(){
        players.sort((p1, p2) -> {
            if (p1.getPotContribution() > p2.getPotContribution())
                return 1;
            else if (p1.getPotContribution() < p2.getPotContribution())
                return -1;
            return 0;
        });
        // List is now sorted by playersPotContribution in ASC order

        List<Player> potWinners;
        float prevContribs = 0; // previous contributions
        float lostContribs = 0; // contributions from folds and loses
        float sidePot =0;
        Iterator<Player> itr = players.iterator();
        Player player ;

        while (itr.hasNext() && potSize !=0){// Iterator
            player = itr.next();
            potWinners=getWinners();
            if (potWinners.contains(player)){ // if is a winner
                if (potWinners.size()==1) {
                    sidePot+=(lostContribs + (player.getPotContribution() - prevContribs) * players.size());
                    lostContribs = 0;
                }
                else {
                    sidePot+=((lostContribs + (player.getPotContribution() - prevContribs) * players.size()) / potWinners.size());
                    lostContribs/=potWinners.size();
                }
                player.setChips(sidePot);
                potSize -=sidePot;
                sidePot=0;

                prevContribs = player.getPotContribution();
            }

            else // if not a winner or hasFolded
                lostContribs += player.getPotContribution()-prevContribs;

            itr.remove();

        }

    }


    @Override
    public String toString() {
        return "Pot{" +
                "amount=" + potSize +
                ", playersInvolved=" + players +
                '}';
    }

}
