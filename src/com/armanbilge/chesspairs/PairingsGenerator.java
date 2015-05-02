package com.armanbilge.chesspairs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

    public PairingsGenerator(final List<Player> players) {
        remainder = new ArrayList<>(players);
        partial = new HashSet<>();
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

        return next;
    }

    public static List<Set<Pair>> generate(final List<Player> players) {
        final List<Set<Pair>> pairings = new ArrayList<>();
        generate(new ArrayList<>(players), new HashSet<>(), pairings);
        return pairings;
    }

    private static void generate(final List<Player> remainder, final Set<Pair> partial, final List<Set<Pair>> pairings) {
        if (remainder.size() == 0)
            pairings.add(partial);
        else {
            IntStream.range(1, remainder.size()).forEach(i ->
                    generate(
                    Stream.concat(remainder.subList(1, i).stream(), remainder.subList(i+1, remainder.size()).stream()).collect(Collectors.toList()),
                    Stream.concat(partial.stream(), Stream.of(new Pair(remainder.get(0), remainder.get(i)))).collect(Collectors.toSet()),
                    pairings));
        }
    }

}
