package com.armanbilge.chesspairs;

/**
 * @author Arman Bilge
 */
public enum Outcome {

    WHITE_WON(1.0, 0.0), BLACK_WON(0.0, 1.0), DRAW(0.5, 0.5);

    private final double whiteScore;
    private final double blackScore;

    Outcome(final double whiteScore, final double blackScore) {
        this.whiteScore = whiteScore;
        this.blackScore = blackScore;
    }

    public double getScore(final Color color) {
        switch (color) {
            case WHITE:
                return whiteScore;
            case BLACK:
                return blackScore;
            default:
                return 0.0;
        }
    }

}
