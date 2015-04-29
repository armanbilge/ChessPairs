package com.armanbilge.chesspairs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;

/**
 * @author Arman Bilge
 */
public class PairingOptimizerImpl implements PairingOptimizer {

    final List<Set<Pair>> bestPairings;

    public PairingOptimizerImpl(final List<Player> players) {
//        bestPairings = StreamSupport.stream(((Iterable<Set<Pair>>) () -> new PairingsGenerator(players)).spliterator(), false).collect(Collectors.toList());
        bestPairings = PairingsGenerator.generate(players).stream()
                .collect(min(PairingOptimizerImpl::getPairingBadness)).stream()
                .collect(min(PairingOptimizerImpl::getColorBadness));
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

    private static Collector<Set<Pair>,List<Set<Pair>>,List<Set<Pair>>> min(ToDoubleFunction<Set<Pair>> f) {
        return Collector.of(ArrayList::new, List::add, (a,b) -> {
            final double x = f.applyAsDouble(a.get(0));
            final double y = f.applyAsDouble(b.get(0));
            if (x < y) {
                return a;
            }
            else if (y > x) {
                return b;
            }
            else {
                a.addAll(b);
                return a;
            }
        });
    }

}
