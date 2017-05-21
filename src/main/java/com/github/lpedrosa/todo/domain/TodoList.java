package com.github.lpedrosa.todo.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

public class TodoList {

    private final ListMultimap<LocalDate, String> entries;

    public TodoList() {
        this.entries = MultimapBuilder.ListMultimapBuilder
                .hashKeys()
                .arrayListValues()
                .build();
    }

    public void store(LocalDate date, String entry) {
        this.entries.put(date, entry);
    }

    public Collection<String> entriesFor(LocalDate date) {
        // return copy since underlying reference allows changes to the entries field
        return new ArrayList<>(this.entries.get(date));
    }

    /**
     * Updates each entry for the given {@code date}, using the {@code updateFunction}.
     *
     * <p>The {@code updateFunction} will run for each entry, allowing the client to transform them.
     * Return {@link Optional#empty()}, when removing a specific entry.
     *
     * @param date the date for which to update the entries
     * @param updateFunction the update function used to update each entry
     */
    public void update(LocalDate date, Function<String, Optional<String>> updateFunction) {
        Collection<String> entries = this.entries.get(date);

        final Collection<String> updatedEntries = entries.stream()
                .map(updateFunction)
                .filter(Optional::isPresent)
                .map(TodoList::unpackOptionalEntries)
                .collect(Collectors.toList());

        this.entries.replaceValues(date, updatedEntries);
    }

    private static String unpackOptionalEntries(Optional<String> entry) {
        return entry.orElseThrow(() -> new IllegalStateException("Error updating entries"));
    }

    public void delete(LocalDate date) {
        this.entries.removeAll(date);
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

}
