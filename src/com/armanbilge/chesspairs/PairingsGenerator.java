package com.armanbilge.chesspairs;

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

    public PairingsGenerator(final Set<Player> players) {
        remainder = players.stream().collect(Collectors.toList());
        partial = new HashSet<>();
    }

    private PairingsGenerator(final List<Player> remainder, final Set<Pair> partial) {
        this.remainder = remainder;
        this.partial = partial;
    }

    @Override
    public boolean hasNext() {
        return hasNext || i < remainder.size();
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
            generator = new PairingsGenerator(remainder.stream().filter(p -> !remainder.get(i).equals(p)).collect(Collectors.toList()),
                    Stream.concat(partial.stream(), Stream.of(new Pair(remainder.get(0), remainder.get(i)))).collect(Collectors.toSet()));
        }

        final Set<Pair> next = generator.next();

        if (!generator.hasNext()) {
            ++i;
            generator = null;
        }

        return next;
    }

}
