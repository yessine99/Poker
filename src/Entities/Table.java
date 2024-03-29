package Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Table implements Serializable {
    private final float blinds;
    private final float minBuyIn;
    private final float maxBuyIn;
    private final List<Boolean> seats = new ArrayList<>();

    public Table() {
        blinds = 100;
        minBuyIn = blinds * 10;
        maxBuyIn = blinds * 100;
    }

    public float getBlinds() {
        return blinds;
    }

    public float getMinBuyIn() {
        return minBuyIn;
    }

    public float getMaxBuyIn(){
        return maxBuyIn;
    }

    public List<Boolean> getSeats() {
        return seats;
    }
}
