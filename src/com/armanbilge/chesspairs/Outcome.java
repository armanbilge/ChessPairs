package com.armanbilge.chesspairs;

/**
 * @author Arman Bilge
 */
public enum Outcome {

    WHITE_WON("White Won", 1.0, 0.0), BLACK_WON("Black Won", 0.0, 1.0), DRAW("Draw", 0.5, 0.5);

    private final String display;
    private final double whiteScore;
    private final double blackScore;

    Outcome(final String display, final double whiteScore, final double blackScore) {
        this.display = display;
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

    public String toString() {
        return display;
    }

}
