package Enums;

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
    CardValue(final int cardValue){
        this.cardValue=cardValue;
    }

    @Override
    public String toString() {

        return switch (cardValue) {
            case 11 -> "J";
            case 12 -> "Q";
            case 13 -> "K";
            case 14 -> "A";
            default -> String.valueOf(cardValue);
        };
    }

}
