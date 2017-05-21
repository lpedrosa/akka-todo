package com.github.lpedrosa.todo.domain;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TodoListTests {

    private TodoList list;

    @Before
    public void setUp() {
        this.list = new TodoList();
    }

    @Test
    public void shouldStartEmpty() {
        assertTrue(list.isEmpty());
    }

    @Test
    public void canStoreAndRetrieveEntries() {
        LocalDate date = LocalDate.now();
        String entry = "Do the laundry";

        list.store(date, entry);

        Collection<String> entriesForDay = list.entriesFor(date);

        assertEquals(1, entriesForDay.size());
        for(String e: entriesForDay) {
            assertEquals(entry, e);
        }
    }

    @Test
    public void retrievingEntriesForEmptyDayShouldReturnNoEntries() {
        LocalDate date = LocalDate.now();

        Collection<String> entriesForDay = list.entriesFor(date);

        assertTrue(entriesForDay.isEmpty());
    }


    @Test
    public void canStoreMultipleEntriesInADay() {
        LocalDate date = LocalDate.now();
        Collection<String> entries = ImmutableList.of("Do the laundry", "Clean the dishes", "Slack off");

        for (String e : entries) {
            list.store(date, e);
        }

        Collection<String> entriesForDay = list.entriesFor(date);

        assertEquals(entries, entriesForDay);
    }

    @Test
    public void canDeleteAllEntriesForAGivenDay() {
        LocalDate date = LocalDate.now();
        Collection<String> entries = ImmutableList.of("Do the laundry", "Clean the dishes", "Slack off");

        for (String e : entries) {
            list.store(date, e);
        }

        list.delete(date);

        assertTrue(list.isEmpty());
    }

    @Test
    public void updateAllowsRemovalOfEntries() {
        LocalDate date = LocalDate.now();
        Collection<String> entries = ImmutableList.of("Do the laundry", "Clean the dishes", "Slack off");

        for (String e : entries) {
            list.store(date, e);
        }

        list.update(date, TodoListTests::removeEntry);

        Collection<String> updatedEntries = list.entriesFor(date);

        Collection<String> expectedEntries = entries.stream()
                .filter(entry -> !entry.equals("Slack off"))
                .collect(Collectors.toList());

        assertEquals(expectedEntries, updatedEntries);
    }

    private static Optional<String> removeEntry(String entry) {
        if (entry.equals("Slack off"))
            return Optional.empty();

        return Optional.of(entry);
    }

    @Test
    public void updateAllowsChangingEntries() {
        LocalDate date = LocalDate.now();
        Collection<String> entries = ImmutableList.of("Do the laundry", "Clean the dishes", "Slack off");

        for (String e : entries) {
            list.store(date, e);
        }

        Function<String, Optional<String>> updateFunction = entry -> Optional.of(entry + " X");
        list.update(date, updateFunction);

        Collection<String> updatedEntries = list.entriesFor(date);

        Collection<String> expectedEntries = entries.stream()
                .map(updateFunction)
                .map(Optional::get)
                .collect(Collectors.toList());

        assertEquals(expectedEntries, updatedEntries);
    }

    @Test
    public void updatingEmptyDayShouldBeNoOp() {
        LocalDate date = LocalDate.now();

        Function<String, Optional<String>> updateFunction = entry -> Optional.of(entry + " X");
        list.update(date, updateFunction);

        Collection<String> updatedEntries = list.entriesFor(date);

        assertTrue(updatedEntries.isEmpty());
    }
}
