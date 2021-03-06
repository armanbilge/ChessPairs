package com.armanbilge.chesspairs;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;
import java.util.stream.StreamSupport;

/**
 * @author Arman Bilge
 */
public class PairingOptimizerImpl implements PairingOptimizer {

    final List<Set<Pair>> bestPairings;

    public PairingOptimizerImpl(final List<Player> players) {
        final PairingsGenerator generator = new PairingsGenerator(players);
        final ProgressBar progress = new ProgressBar();
        progress.progressProperty().bind(generator.progressProperty());
        final Stage stage = new Stage();
        stage.setTitle("Generating Pairings...");
        stage.setScene(new Scene(new HBox(new Label("Hello"))));
        stage.show();
        progress.progressProperty().addListener((o, ov, nv) -> {
            if (nv.equals(1.0)) stage.hide();
        });
        bestPairings = StreamSupport.stream(
                ((Iterable<Set<Pair>>) () -> generator).spliterator(), false)
                .collect(min(PairingOptimizerImpl::getPairingBadness)).stream()
                .map(pairing -> {optimizeColors(pairing); return pairing;})
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
        return Collector.of(ArrayList::new,
                (a,b) -> {
                    final double x = a.size() > 0 ? f.applyAsDouble(a.get(0)) : Double.POSITIVE_INFINITY;
                    final double y = f.applyAsDouble(b);
                    if (x == y) {
                        a.add(b);
                    } else if (y < x) {
                        a.clear();
                        a.add(b);
                    }
                },
                (a,b) -> {
                    final double x = a.size() > 0 ? f.applyAsDouble(a.get(0)) : Double.POSITIVE_INFINITY;
                    final double y = b.size() > 0 ? f.applyAsDouble(b.get(0)) : Double.POSITIVE_INFINITY;
                    if (x < y) {
                        return a;
                    } else if (y < x) {
                        return b;
                    } else {
                        a.addAll(b);
                        return a;
                    }
            });
    }

}
