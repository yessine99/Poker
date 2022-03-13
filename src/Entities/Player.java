package Entities;

import java.util.List;
import java.util.Scanner;

public class Player {
    private int id;
    private Hand hand = new Hand();
    private float chips;
    private boolean hasFolded;
    private boolean isAllIn;
    private boolean hasPlayed;
    private float currentBet;



    public Hand getHand() {
        return hand;
    }

    public Player(int id) {

        this.id = id;
        hasFolded =false;
        hasPlayed=false;
        chips=500000;
    }

    public float getCurrentBet() {
        return currentBet;
    }

    public void setCurrentBet(float currentBet) {
        this.currentBet = currentBet;
    }

    public boolean isHasFolded() {
        return hasFolded;
    }

    public boolean isHasPlayed() {
        return hasPlayed;
    }

    public void setHasPlayed(boolean hasPlayed) {
        this.hasPlayed = hasPlayed;
    }

    public float raise(float prevBet, List<Player> players){
        float amount=0;
        Scanner input = new Scanner(System.in);
        System.out.print("Amount :"+prevBet*2+" + :");
        amount = input.nextFloat()+prevBet*2;
        chips-=amount - currentBet;
        if (chips==0)
            isAllIn=true;
        currentBet=amount;
        players.stream().forEach(player->player.setHasPlayed(false));
        hasPlayed=true;

        return amount;
    }


    public void setChips(float chips) {
        this.chips += chips;
    }

    public void call(float amount){
        if (amount>=chips){
            chips=0;
            isAllIn=true;
            currentBet=chips;
        }
        else {
            chips -= amount - currentBet;
            currentBet = amount;
        }
        hasPlayed=true;
    }
    public void fold(){
        hasFolded = true;
    }

    public void check(){
        hasPlayed=true;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return id == player.id;
    }

    public int getId() {
        return id;
    }

    public Player() {
    }
    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", chips=" + chips+
                '}';
    }

    @Override
    public int hashCode() {
        return id;
    }
}
