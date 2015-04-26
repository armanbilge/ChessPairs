package com.armanbilge.chesspairs;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Arman Bilge
 */
public class PairingOptimizer {

    final List<Set<Pair>> bestPairings;

    public PairingOptimizer(final Set<Player> players) {
        bestPairings = StreamSupport.stream(((Iterable<Set<Pair>>) () -> new PairingsGenerator(players)).spliterator(), false).collect(Collectors.toList());
        final double min1 = bestPairings.stream().mapToDouble(PairingOptimizer::getPairingBadness).min().getAsDouble();
        bestPairings.removeIf(p -> getPairingBadness(p) > min1);
        bestPairings.forEach(PairingOptimizer::optimizeColors);
        final double min2 = bestPairings.stream().mapToDouble(PairingOptimizer::getColorBadness).min().getAsDouble();
        bestPairings.removeIf(p -> getPairingBadness(p) > min2);
    }

    public Set<Pair> getRandomPairing() {
        final Set<Pair> pairing = bestPairings.get(ChessPairs.RANDOM.nextInt(bestPairings.size()));
        optimizeColors(pairing);
        return pairing;
    }

    private static double getPairingBadness(final Set<Pair> pairing) {
        return pairing.stream().mapToDouble(Pair::getPairBadness).sum();
    }

    private static double getColorBadness(final Set<Pair> pairing) {
        return pairing.stream().mapToDouble(Pair::getColorBadness).sum();
    }

    private static void optimizeColors(final Set<Pair> pairing) {
        pairing.forEach(Pair::optimizeColors);
    }

}
