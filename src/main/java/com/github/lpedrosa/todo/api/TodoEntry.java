package com.github.lpedrosa.todo.api;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TodoEntry {

    private final String owner;
    private final LocalDate date;
    private final String entry;

    @JsonCreator
    private TodoEntry(@JsonProperty("owner") String owner,
                      @JsonProperty("date") String date,
                      @JsonProperty("entry") String entry) {
        this.owner = owner;
        this.date = parseDate(date);
        this.entry = entry;
    }

    private static LocalDate parseDate(String date) {
        final LocalDate d;
        try {
            d = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("The given date " + date + " does not respect the following format yyyy-MM-dd", ex);
        }
        return d;
    }

    public String getOwner() {
        return owner;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getEntry() {
        return entry;
    }

}
