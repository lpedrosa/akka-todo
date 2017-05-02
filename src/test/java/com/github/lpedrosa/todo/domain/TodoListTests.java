package com.github.lpedrosa.todo.domain;

import java.time.LocalDate;
import java.util.Collection;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TodoListTests {

    @Test
    public void shouldStartEmpty() {
        TodoList list = new TodoList("test");

        assertTrue(list.isEmpty());
    }

    @Test
    public void canStoreAndRetrieveEntries() {
        TodoList list = new TodoList("test");

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
        TodoList list = new TodoList("test");

        LocalDate date = LocalDate.now();

        Collection<String> entriesForDay = list.retrieveEntries(date);

        assertTrue(entriesForDay.isEmpty());
    }


    @Test
    public void canStoreMultipleEntriesInADay() {
        TodoList list = new TodoList("test");

        LocalDate date = LocalDate.now();
        Collection<String> entries = ImmutableList.of("Do the laundry", "Clean the dishes", "Slack off");

        for (String e : entries) {
            list.storeEntry(date, e);
        }

        Collection<String> entriesForDay = list.retrieveEntries(date);

        assertEquals(entries, entriesForDay);
    }

}
