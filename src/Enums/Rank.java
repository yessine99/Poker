package Enums;

public enum Rank { // Best 5 Cards combo score is (2^14)*4 + 2^13 = 73728 --> AAAA+K
    // so  a step of at least 73728 between each rank
    HighCard(0),
    Pair(100),
    TwoPairs(200),
    ThreeOfAKind(300),
    Straight(400),
    Flush(500),
    FullHouse(600),
    FourOfAKind(700),
    StraightFlush(800);


    private final int rank;

    Rank(final int rank){
        this.rank=rank;
    }


}
