package com.armanbilge.chesspairs;

import java.io.Serializable;

/**
 * @author Arman Bilge
 */
public class Game implements Serializable {

    private static final long serialVersionUID = 0;

    private final Player white;
    private final boolean whiteNoCount;
    private final Player black;
    private final boolean blackNoCount;
    private final Outcome outcome;

    public Game(final Player white,
                final boolean whiteNoCount,
                final Player black,
                final boolean blackNoCount,
                final Outcome outcome) {
        this.white = white;
        this.whiteNoCount = whiteNoCount;
        this.black = black;
        this.blackNoCount = blackNoCount;
        this.outcome = outcome;
    }

    public Player getWhite() {
        return white;
    }

    public Player getBlack() {
        return black;
    }

    public Color getColor(final Player player) {
        if (getWhite().equals(player))
            return Color.WHITE;
        else if (getBlack().equals(player))
            return Color.BLACK;
        else
            return null;
    }

    public Color getNullableColor(final Player player) {
        if (getWhite().equals(Unpaired.INSTANCE) || getBlack().equals(Unpaired.INSTANCE))
            return null;
        else if (getWhite().equals(player))
            return Color.WHITE;
        else if (getBlack().equals(player))
            return Color.BLACK;
        else
            return null;
    }

    public double getScore(final Player player) {
        if (counted(player))
            return outcome.getScore(getColor(player));
        else
            return 0;
    }

    public String getOutcome(final Player player) {
        if (outcome == null)
            return "NA";
        final String won = "won";
        final String lost = "lost";
        switch (outcome) {
            case WHITE_WON: {
                switch (getColor(player)) {
                    case WHITE:
                        return won;
                    case BLACK:
                        return lost;
                }
            }
            case BLACK_WON: {
                switch (getColor(player)) {
                    case WHITE:
                        return lost;
                    case BLACK:
                        return won;
                }
            }
            case DRAW: return "draw";
        }
        return null;
    }

    public boolean counted(final Player player) {
        switch (getColor(player)) {
            case WHITE:
                return !whiteNoCount;
            case BLACK:
                return !blackNoCount;
            default:
                return false;
        }
    }

    public Player getOpponent(final Player player) {
        switch (getColor(player)) {
            case WHITE:
                return black;
            case BLACK:
                return white;
            default:
                return null;
        }
    }

}
