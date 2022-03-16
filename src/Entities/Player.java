package Entities;

import java.util.List;
import java.util.Scanner;

public class Player {

    private int id;
    private Hand hand = new Hand();
    private float chips;
    private boolean hasPlayed;
    private boolean hasFolded;
    private boolean isAllIn;
    private float currentBet;
    private float potContribution;


    public Player() {
    }

    public Player(int id) {
        this.id=id;
        hasFolded =false;
        hasPlayed=false;
        isAllIn=false;
        chips=id*10000;
        potContribution=0;
        currentBet=0;
    }

    public int getId() {
        return id;
    }

    public Hand getHand() {
        return hand;
    }

    public float getChips() {
        return chips;
    }

    public boolean hasPlayed() {
        return hasPlayed;
    }

    public boolean hasFolded() {
        return hasFolded;
    }

    public boolean isAllIn() {
        return isAllIn;
    }

    public float getCurrentBet() {
        return currentBet;
    }

    public float getPotContribution() {
        return potContribution;
    }


    public void initForNextRound(){
        hasPlayed = false;
        potContribution += currentBet;
        currentBet = 0;
    }

    public void setBlind(float blind){
        currentBet = blind;
        chips -= blind;
    }

    public void initForNextHand(){ //for next hand
        hand.clear();
        potContribution=0;
        if (hasFolded)
            hasFolded=false;
        else if (isAllIn)
            isAllIn=false;
        if (hasPlayed)
            hasPlayed=false;
    }

    public void addChips(float chips) {
        this.chips += chips;
    }

    public float raise(float prevBet, List<Player> players){
        float amount;
        Scanner input = new Scanner(System.in);
        System.out.print("Amount :"+prevBet*2+" + :");
        amount = input.nextFloat()+prevBet*2;

        if (amount - currentBet >=chips){
            isAllIn=true;
            currentBet+=chips;
            chips=0;
        }
        else
        {
            chips -= amount - currentBet;
            currentBet = amount;
        }
        System.out.println(currentBet);
        hasPlayed=true;
        return currentBet;
    }

    public void call(float amount){
        if (amount>=chips){
            currentBet+=chips;
            chips=0;
            isAllIn=true;
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
        return id == player.getId();
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", chips=" + chips+
                ",Pot contribution ="+potContribution+
                ",\t\tHand :"+hand.getCards()+
                '}';
    }

    @Override
    public int hashCode() {
        return id;
    }
}
