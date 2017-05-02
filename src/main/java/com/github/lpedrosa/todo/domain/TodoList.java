package com.github.lpedrosa.todo.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

public class TodoList {

    private final String owner;
    private final ListMultimap<LocalDate, String> entries;

    public TodoList(String owner) {
        this.owner = owner;
        this.entries = MultimapBuilder.ListMultimapBuilder
                .hashKeys()
                .arrayListValues()
                .build();
    }

    public String getOwner() {
        return owner;
    }

    public void storeEntry(LocalDate date, String entry) {
        this.entries.put(date, entry);
    }

    public Collection<String> retrieveEntries(LocalDate date) {
        // return copy since underlying reference allows changes to the entries field
        return new ArrayList<>(this.entries.get(date));
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

}
