package com.armanbilge.chesspairs;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Arman Bilge
 */
public class Tournament implements Serializable {

    private static final long serialVersionUID = 0;

    private final ObservableList<Player> players;
    private final ObservableList<Game> games;

    public Tournament() {
        this.players = new ObservableListWrapper<>(new ArrayList<>());
        this.games = new ObservableListWrapper<>(new ArrayList<>());
    }

    public void addPlayer(final Player player) {
        players.add(player);
        players.sort((p, o) -> p.getName().compareTo(o.getName()));
    }

    public void removePlayer(final Player player) {
        players.remove(player);
    }

    public void addGame(final Game game) {
        games.add(game);
        game.getWhite().addGame(game);
        game.getBlack().addGame(game);
    }

    public void removeGame(final Game game) {
        games.remove(game);
        game.getWhite().removeGame(game);
        game.getBlack().removeGame(game);
    }

    public void write(final File file) throws IOException {
        try (final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(this);
        }
    }

    public static Tournament read(final File file) throws IOException, ClassNotFoundException {
        try (final ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (Tournament) in.readObject();
        }
    }

    public ObservableList<Player> getPlayers() {
        return players;
    }

    public ObservableList<Game> getGames() {
        return games;
    }

}
