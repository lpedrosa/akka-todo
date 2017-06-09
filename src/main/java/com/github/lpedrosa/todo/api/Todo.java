package com.github.lpedrosa.todo.api;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Todo {
    private final String title;
    private final LocalDate date;
    private final Collection<String> entries;

    public Todo(String title, LocalDate date, Collection<String> entries) {
        this.title = title;
        this.date = date;
        this.entries = entries;
    }

    @JsonProperty("title")
    public String getOwner() {
        return this.title;
    }

    @JsonProperty("date")
    public String getFormattedDate() {
        return this.date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @JsonProperty("entries")
    public Collection<String> getEntries() {
        return this.entries;
    }
}
