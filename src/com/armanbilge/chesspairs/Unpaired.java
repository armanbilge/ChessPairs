package com.armanbilge.chesspairs;

/**
 * @author Arman Bilge
 */
public final class Unpaired extends Player {

    public Unpaired() {
        super("UNPAIRED");
    }

    @Override
    public double getScore() {
        return 0;
    }

    @Override
    public double getColorBadness(final Color color) {
        return 0;
    }

}
