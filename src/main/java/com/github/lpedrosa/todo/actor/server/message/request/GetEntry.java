package com.github.lpedrosa.todo.actor.server.message.request;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.google.common.base.MoreObjects;

public class GetEntry {

    private final LocalDate date;

    public GetEntry(LocalDate date) {
        this.date = Objects.requireNonNull(date);
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("date", this.date.format(DateTimeFormatter.BASIC_ISO_DATE))
                .toString();
    }
}
