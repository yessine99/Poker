import Entities.*;
import java.io.Serializable;

public class GameState implements Serializable {

    private final int id = 1; // must be changed
    private float prevBet =0;
    private int allinPlayers;
    private int foldedPlayers;
    private int bigBlindIndex;
    private int smallBlindIndex;
    private  float pot =0;
    private final Deck deck = new Deck();
    private final Table table = new Table();
    private boolean onGoing;

    public GameState() {
        onGoing = false;
        bigBlindIndex=0;
    }


    public int getId() {
        return id;
    }

    public void setAllinPlayers(int allinPlayers) {
        this.allinPlayers = allinPlayers;
    }

    public void setFoldedPlayers(int foldedPlayers) {
        this.foldedPlayers = foldedPlayers;
    }


    public void incrementBlindsIndexes(int numberOfPlayers){
        bigBlindIndex++;

        if(bigBlindIndex==0)
            smallBlindIndex = numberOfPlayers-1;
        else
            smallBlindIndex = bigBlindIndex - 1;
    }

    public float getPrevBet() {
        return prevBet;
    }

    public void setPrevBet(float prevBet) {
        this.prevBet = prevBet;
    }

    public void incrementAllinPlayers(){
        allinPlayers++;
    }

    public int getFoldedPlayers() {
        return foldedPlayers;
    }

    public void incrementFoldedPlayers() {
        foldedPlayers++;
    }

    public float getPot() {
        return pot;
    }

    public void setPot(float pot) {
        this.pot = pot;
    }

    public boolean isOnGoing() {
        return onGoing;
    }

    public void setOnGoing(boolean onGoing) {
        this.onGoing = onGoing;
    }

    public int getAllinPlayers() {
        return allinPlayers;
    }

    public int getBigBlindIndex() {
        return bigBlindIndex;
    }

    public int getSmallBlindIndex() {
        return smallBlindIndex;
    }

    public Deck getDeck() {
        return deck;
    }

    public Table getTable() {
        return table;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameState gameState)) return false;
        return id == gameState.getId();
    }

    @Override
    public String toString() {
        return "GameState{" +
                ", onGoing= " + onGoing +
                ", "+pot+
                '}';
    }

}
