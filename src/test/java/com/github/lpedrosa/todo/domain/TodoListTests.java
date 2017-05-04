package com.github.lpedrosa.todo.domain;

import java.time.LocalDate;
import java.util.Collection;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.Before;

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

        list.storeEntry(date, entry);

        Collection<String> entriesForDay = list.retrieveEntries(date);

        assertEquals(1, entriesForDay.size());
        for(String e: entriesForDay) {
            assertEquals(entry, e);
        }
    }

    @Test
    public void retrievingEntriesForEmptyDayShouldReturnNoEntries() {
        LocalDate date = LocalDate.now();

        Collection<String> entriesForDay = list.retrieveEntries(date);

        assertTrue(entriesForDay.isEmpty());
    }


    @Test
    public void canStoreMultipleEntriesInADay() {
        LocalDate date = LocalDate.now();
        Collection<String> entries = ImmutableList.of("Do the laundry", "Clean the dishes", "Slack off");

        for (String e : entries) {
            list.storeEntry(date, e);
        }

        Collection<String> entriesForDay = list.retrieveEntries(date);

        assertEquals(entries, entriesForDay);
    }

}
