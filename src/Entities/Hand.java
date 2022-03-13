package Entities;


import java.util.*;

public class Hand implements Comparable<Hand>{
    private List<Card> hand = new ArrayList<>();
    private double score;

    public List<Card> getHandCards() {
        return hand;
    }

    public double getScore() {
        return score;
    }

    public Hand() {

    }

    public boolean isPair(){
        for (int i=0; i<hand.size()-2;i++){
            if (hand.get(i).getValue()==hand.get(i+1).getValue()){
                score=Rank.Pair.ordinal()*100000+Math.pow(2,hand.get(i).getValue())*2;
                return true;
            }
        }
        return false;
    }

    public boolean isTwoPair(){
        int counter = 0;
        int tmpScore=0;
        if  (hand.get(0).getValue()==hand.get(1).getValue()){
            tmpScore+= Math.pow(2,hand.get(0).getValue())*2;
            counter++;
        }

        for (int i=counter+1; i<hand.size()-1;i++){
            if(hand.get(i).getValue()==hand.get(i+1).getValue() && hand.get(i).getValue()!=hand.get(i-1).getValue()){
                tmpScore+= Math.pow(2,hand.get(i).getValue())*2;
                counter++;
                if (counter==2){
                    score =Rank.TwoPairs.ordinal()*100000+tmpScore;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isThreeOfAKind(){
        int j;
        for (int i=0; i<hand.size()-2;i++){
            j=i+1;
            while (j<i+3 && hand.get(i).getValue()==hand.get(j).getValue()){
                j++;
            }
            if (j==i+3){
                score =Rank.ThreeOfAKind.ordinal()*100000 + Math.pow(2,hand.get(i).getValue())*3;
                return true;
            }
        }
        return false;
    }

    public boolean isFourOfAKind(){
        int j;
        for (int i=0; i<hand.size()-3;i++){
            j=i+1;
            while (j<i+4 && hand.get(i).getValue()==hand.get(j).getValue()){
                j++;
            }
            if (j==i+4){
                score =Rank.FourOfAKind.ordinal()*100000+hand.get(i).getValue();
                return true;
            }
        }
        return false;
    }

    public boolean isFullHouse(){
        int i=0;
        boolean isPair=false;
        boolean isThreeOfAKind=false;
        int counter;
        int j;
        double tmpScore=0;

        while (i<hand.size()-1 && ( !isPair || !isThreeOfAKind )){
            counter=1;
            j=i+1;

            while (j<hand.size() && hand.get(i).getValue()==hand.get(j).getValue() ){
                counter ++;
                j++;
            }

            if (counter==3 && !isThreeOfAKind){
                isThreeOfAKind=true;
                tmpScore+=Math.pow(2,hand.get(i).getValue())*3;
            }

            else if (counter >1 && !isPair){
                isPair=true;
                tmpScore+=(Math.pow(2,hand.get(i).getValue())*2)/16384;
            }

            i+=counter;
        }

        if(isPair && isThreeOfAKind){
            score = Rank.FullHouse.ordinal()*100000 + tmpScore;
            return true;
        }
        return false;
    }

    public boolean isFlush(){
        int counter;
        double tmpScore;
        for (int i=0; i<hand.size()-1;i++){
            counter = 1;
            tmpScore=0;
            for (int j=i+1; j<hand.size();j++ ){
                if (hand.get(i).getSuit().equals(hand.get(j).getSuit())){
                    tmpScore+=Math.pow(2,hand.get(j).getValue());
                    counter ++;

                    if (counter==5){
                        score =Rank.Flush.ordinal()*100000+tmpScore+Math.pow(2,hand.get(i).getValue());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isStraight(){
        int counter=1;
        for (int i= 0 ; i<hand.size()-1;i++)
        {
            if(hand.get(i).getValue()==hand.get(i+1).getValue() || hand.get(i).getValue()==hand.get(i+1).getValue()+1){
                if (hand.get(i).getValue()==hand.get(i+1).getValue()+1){
                    counter++;
                    if (counter == 4 && hand.get(i+1).getValue()==2 && hand.get(0).getValue()==14) // A + 2345
                    {
                        score =Rank.Straight.ordinal()*100000+15;
                        return true;
                    }
                    if(counter==5 ){
                        score =Rank.Straight.ordinal()*100000+hand.get(i+1).getValue()*5+10;
                        return true;
                    }
                }
            }
            else
                counter=1;
        }
        return false;
    }

    public boolean isStraightFlush(){
        int counter=1;
        List<Card> flushList = new ArrayList<>();
        int i=0;
        int j;
        while(i<hand.size()-1 && counter<5){ // Extracting a possible Flush
            counter = 1;
            flushList.clear();
            flushList.add(hand.get(i));
            j=i+1;

            while (j<hand.size()){
                if (hand.get(i).getSuit().equals(hand.get(j).getSuit())){
                    counter ++;
                    flushList.add(hand.get(j));
                }
                j++;
            }
            i++;
        }

        if (counter >= 5){ // check if the flushList is a Straight
            counter=1;

            for (i= 0 ; i<flushList.size()-1;i++)
                if(flushList.get(i).getValue()==flushList.get(i+1).getValue()+1){
                    counter ++;
                    if (counter == 4 && flushList.get(i+1).getValue()==2 && flushList.get(0).getValue()==14){
                        score =Rank.StraightFlush.ordinal()*100000+15;
                        return true;
                    }
                }

            if(counter==5 ){
                score =Rank.StraightFlush.ordinal()*100000+flushList.get(i).getValue()*5+10;
                return true;
            }
        }
        return false;
    }

    public String evaluateHand(){
        Collections.sort(hand,Collections.reverseOrder()); // Hand must be sorted in DESC order

        if (isStraightFlush())
            return "Straight Flush";
        else if (isFourOfAKind())
            return "Four of A Kind ";
        else if (isFullHouse())
            return "Full House ";
        else if (isFlush())
            return "Flush";
        else if (isStraight())
            return "Straight";
        else if (isThreeOfAKind())
            return "Three of a Kind";
        else if (isTwoPair())
            return "Two Pair";
        else if (isPair())
            return "Pair";
        else {
            score =Rank.HighCard.ordinal();
            return "High Card";
        }


    }

    @Override
    public String toString() {
        return "Hand{"
                 + hand +
                '}';
    }

    @Override
    public int compareTo(Hand otherHand) {
        if(score<Rank.Straight.ordinal()*100000) // if score < Straight.score then compare Kickers
            for (int i=0; i<5;i++){
                if (hand.get(i).getValue() != otherHand.getHandCards().get(i).getValue())
                    return hand.get(i).getValue() - otherHand.getHandCards().get(i).getValue();
            }
        return 0;
    }
}
