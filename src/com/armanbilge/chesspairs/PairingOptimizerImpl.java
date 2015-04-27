package com.armanbilge.chesspairs;

import java.util.List;
import java.util.Set;

/**
 * @author Arman Bilge
 */
public class PairingOptimizerImpl implements PairingOptimizer {

    final List<Set<Pair>> bestPairings;

    public PairingOptimizerImpl(final Set<Player> players) {
//        bestPairings = StreamSupport.stream(((Iterable<Set<Pair>>) () -> new PairingsGenerator(players)).spliterator(), false).collect(Collectors.toList());
        bestPairings = PairingsGenerator.generate(players);
        double[] x = bestPairings.stream().mapToDouble(PairingOptimizerImpl::getPairingBadness).toArray();
        final double min1 = bestPairings.stream().mapToDouble(PairingOptimizerImpl::getPairingBadness).min().getAsDouble();
        bestPairings.removeIf(p -> getPairingBadness(p) > min1);
        bestPairings.forEach(PairingOptimizerImpl::optimizeColors);
        final double min2 = bestPairings.stream().mapToDouble(PairingOptimizerImpl::getColorBadness).min().getAsDouble();
        bestPairings.removeIf(p -> getColorBadness(p) > min2);
    }

    @Override
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
