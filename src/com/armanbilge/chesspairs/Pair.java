package com.armanbilge.chesspairs;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author Arman Bilge
 */
public class Pair {

    private final Player player1;
    private final Player player2;
    private ObjectProperty<Player> white = new SimpleObjectProperty<>();
    private ObjectProperty<Player> black = new SimpleObjectProperty<>();

    private static final int OPPONENT_FACTOR = 8192;
    private static final int SCORE_FACTOR = 512;
    private static final int COLOR_FACTOR = 1;

    public Pair(final Player player1, final Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public Player getWhite() {
        return whiteProperty().get();
    }

    public Player getBlack() {
        return blackProperty().get();
    }

    public ObjectProperty<Player> whiteProperty() {
        return white;
    }

    public ObjectProperty<Player> blackProperty() {
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
        final Player white, black;
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
        whiteProperty().set(white);
        blackProperty().set(black);
    }

    public double getColorBadness() {
        return COLOR_FACTOR * calculateColorBadness(getWhite(), getBlack());
    }

    private static double calculateColorBadness(final Player white, final Player black) {
        return white.getColorBadness(Color.WHITE) + black.getColorBadness(Color.BLACK);
    }

}
