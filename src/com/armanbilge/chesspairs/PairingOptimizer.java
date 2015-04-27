package com.armanbilge.chesspairs;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Arman Bilge
 */
public interface PairingOptimizer {

    Set<Pair> getRandomPairing();

    static PairingOptimizer create(final Tournament tournament) {
        if (tournament.getGames().size() > 0)
            return new PairingOptimizerImpl(new HashSet<>(tournament.getPlayers()));
        else
            return new RandomPairing(tournament.getPlayers());
    }
}
