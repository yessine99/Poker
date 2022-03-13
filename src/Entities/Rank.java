package Entities;

public enum Rank { // Best 5 Cards combo score is (2^14)*4 + 2^13 --> AAAA+K
    // so  a step of 73728 is enough between each rank (not 100 000)
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

    private Rank(final int rank){
        this.rank=rank;
    }


}
