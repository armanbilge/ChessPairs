package com.armanbilge.chesspairs;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Arman Bilge
 */
public class PairingsGenerator implements Iterator<Set<Pair>> {

    private final List<Player> remainder;
    private final Set<Pair> partial;

    private boolean hasNext = true;
    private int i = 1;
    private PairingsGenerator generator = null;

    private int progress = 0;
    private int total = -1;
    private DoubleProperty progressProperty = new SimpleDoubleProperty(0);

    public PairingsGenerator(final List<Player> players) {
        remainder = new ArrayList<>(players);
        partial = new HashSet<>();
        total = calculateTotal(players.size());
    }

    private PairingsGenerator(final List<Player> remainder, final Set<Pair> partial) {
        this.remainder = remainder;
        this.partial = partial;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public Set<Pair> next() {

        if (!hasNext())
            throw new NoSuchElementException();

        if (remainder.isEmpty()) {
            hasNext = false;
            return partial;
        }

        if (generator == null) {
            generator = new PairingsGenerator(Stream.concat(remainder.subList(1, i).stream(), remainder.subList(i+1, remainder.size()).stream()).collect(Collectors.toList()),
                    Stream.concat(partial.stream(), Stream.of(new Pair(remainder.get(0), remainder.get(i)))).collect(Collectors.toSet()));
        }

        final Set<Pair> next = generator.next();

        if (!generator.hasNext()) {
            ++i;
            hasNext = i < remainder.size();
            generator = null;
        }

        if (total > 0)
            progressProperty.set(++progress / (double) total);

        return next;
    }

    public DoubleProperty progressProperty() {
        return progressProperty;
    }

    private static int calculateTotal(int n) {
        int t = 1;
        for (--n; n > 1; n -= 2)
            t *= n;
        return t;
    }

}
