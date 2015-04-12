package com.armanbilge.chesspairs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arman Bilge
 */
public class Player implements Serializable {

    private final String first;
    private final String last;
    private final List<Game> games;

    public Player(final String first,
                  final String last) {
        this.first = first;
        this.last = last;
        games = new ArrayList<>();
    }

    void addGame(final Game game) {
        games.add(game);
    }

    void removeGame(final Game game) {
        games.remove(game);
    }

    public double getScore() {
        return games.stream().mapToDouble(game -> game.getScore(this)).sum();
    }

    public boolean hasPlayed(final Player player) {
        return games.stream().map(game -> game.getOpponent(player)).anyMatch(op -> op.equals(player));
    }

    public double getColorBadness(final Color color) {
        final List<Color> colors = games.stream().map(game -> game.getColor(this)).collect(Collectors.toList());
        final int dif = 2 * Collections.frequency(colors, color) + 1 - colors.size();
        final int last = color == colors.get(colors.size() - 1) ? 1 : 0;
        return Math.pow(dif, 2) + last;
    }

}
