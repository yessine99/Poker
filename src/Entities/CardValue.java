package Entities;

public enum CardValue {
    Duce(2),
    Three(3),
    Four(4),
    Five(5),
    Six(6),
    Seven(7),
    Eight(8),
    Nine(9),
    Ten(10),
    Jack(11),
    Queen(12),
    King(13),
    Ace(14);


    private final int cardValue;
    private CardValue(final int cardValue){
        this.cardValue=cardValue;
    }

    @Override
    public String toString() {

            switch(cardValue){
                case 11:
                    return "J";
                case 12:
                    return "Q";
                case 13 :
                    return "K";
                case 14:
                    return "A";
                default:return String.valueOf(cardValue);
            }
    }

}
