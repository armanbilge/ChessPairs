package com.armanbilge.chesspairs;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arman Bilge
 */
public class Player implements Serializable {

    private static final long serialVersionUID = 0;

    private final String name;
    private final ObservableList<Game> games;

    public Player(final String name) {
        this.name = name;
        games = new ObservableListWrapper<>(new ArrayList<>());
    }

    void addGame(final Game game) {
        games.add(game);
    }

    void removeGame(final Game game) {
        games.remove(game);
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getName();
    }

    public ObservableList<Game> getGames() {
        return games;
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
