package com.armanbilge.chesspairs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Arman Bilge
 */
public class RandomPairing implements PairingOptimizer {

    private final List<Player> players;

    public RandomPairing(final List<Player> players) {
        this.players = new ArrayList<>(players);
    }

    @Override
    public Set<Pair> getRandomPairing() {
        Collections.shuffle(players, ChessPairs.RANDOM);
        return IntStream.range(0, players.size() / 2)
                .map(i -> i * 2)
                .mapToObj(i -> {
                    final Pair p = new Pair(players.get(i), players.get(i+1));
                    p.optimizeColors();
                    return p;
                }).collect(Collectors.toSet());
    }

}
