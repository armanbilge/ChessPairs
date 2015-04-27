package com.armanbilge.chesspairs;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
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
    private final List<Game> games;

    private transient StringProperty nameProperty = null;
    private transient DoubleProperty score = null;
    private transient ObservableList<Game> observableGames = null;

    public Player(final String name) {
        this.name = name;
        games = new ArrayList<>();
    }

    void addGame(final Game game) {
        getGames().add(game);
    }

    void removeGame(final Game game) {
        getGames().remove(game);
    }

    public String getName() {
        return nameProperty().getValue();
    }

    public StringProperty nameProperty() {
        if (nameProperty == null)
            nameProperty = new SimpleStringProperty(name);
        return nameProperty;
    }

    public String toString() {
        return getName();
    }

    public ObservableList<Game> getGames() {
        if (observableGames == null) {
            observableGames = new ObservableListWrapper<>(games);
            observableGames.addListener((ListChangeListener<? super Game>) c -> scoreProperty().set(calculateScore()));
        }
        return observableGames;
    }

    public double getScore() {
        return scoreProperty().get();
    }

    private double calculateScore() {
        return getGames().stream().mapToDouble(game -> game.getScore(this)).sum();
    }

    public DoubleProperty scoreProperty() {
        if (score == null)
            score = new SimpleDoubleProperty(calculateScore());
        return score;
    }

    public boolean hasPlayed(final Player player) {
        return getGames().stream().map(game -> game.getOpponent(this)).anyMatch(player::equals);
    }

    public double getColorBadness(final Color color) {
        final List<Color> colors = getGames().stream()
                .filter(game -> game.counted(this))
                .map(game -> game.getColor(this))
                .collect(Collectors.toList());
        final int dif = 2 * Collections.frequency(colors, color) + 1 - colors.size();
        final int last = colors.size() > 0 && color == colors.get(colors.size() - 1) ? 1 : 0;
        return Math.pow(dif, 2) + last;
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof Player)
            return getName().equals(((Player) other).getName());
        return false;
    }

}
