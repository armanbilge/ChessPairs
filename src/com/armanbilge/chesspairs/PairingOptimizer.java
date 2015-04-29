package com.armanbilge.chesspairs;

import java.util.Set;

/**
 * @author Arman Bilge
 */
public interface PairingOptimizer {

    Set<Pair> getRandomPairing();

    static PairingOptimizer create(final Tournament tournament) {
        if (tournament.getGames().size() > 0)
            return new PairingOptimizerImpl(tournament.getPlayers());
        else
            return new RandomPairing(tournament.getPlayers());
    }
}
