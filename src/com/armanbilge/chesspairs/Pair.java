package com.armanbilge.chesspairs;

/**
 * @author Arman Bilge
 */
public class Pair {

    private final Player player1;
    private final Player player2;
    private Player white;
    private Player black;

    private static final int OPPONENT_FACTOR = 8192;
    private static final int SCORE_FACTOR = 512;
    private static final int COLOR_FACTOR = 1;

    public Pair(final Player player1, final Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public Player getWhite() {
        return white;
    }

    public Player getBlack() {
        return black;
    }

    public double getPairBadness() {
        double badness = 0;
        if (player1.hasPlayed(player2))
            badness += OPPONENT_FACTOR;
        final double delta = player1.getScore() - player2.getScore();
        badness += SCORE_FACTOR * delta * delta;
        return badness;
    }

    public void optimizeColors() {
        final double a = calculateColorBadness(player1, player2);
        final double b = calculateColorBadness(player2, player1);
        if (a < b) {
            white = player1;
            black = player2;
        } else if (a > b) {
            white = player2;
            black = player1;
        } else if (ChessPairs.RANDOM.nextBoolean()) {
            white = player1;
            black = player2;
        } else {
            white = player2;
            black = player1;
        }
    }

    public double getColorBadness() {
        return COLOR_FACTOR * calculateColorBadness(white, black);
    }

    private static double calculateColorBadness(final Player white, final Player black) {
        return white.getColorBadness(Color.WHITE) + black.getColorBadness(Color.BLACK);
    }

}
