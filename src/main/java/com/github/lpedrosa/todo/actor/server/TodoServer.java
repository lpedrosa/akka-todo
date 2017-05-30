package com.github.lpedrosa.todo.actor.server;

import java.time.LocalDate;
import java.util.Collection;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.event.slf4j.Logger;
import com.github.lpedrosa.todo.actor.server.message.reply.Entry;
import com.github.lpedrosa.todo.actor.server.message.request.AddEntry;
import com.github.lpedrosa.todo.actor.server.message.request.GetEntry;
import com.github.lpedrosa.todo.domain.TodoList;

public class TodoServer extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private final String owner;
    private final TodoList list;

    public static Props props(String owner) {
        return Props.create(TodoServer.class, () -> new TodoServer(owner));
    }

    private TodoServer(String owner) {
        this.owner = owner;
        this.list = new TodoList();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AddEntry.class, this::doAddEntry)
                .match(GetEntry.class, this::doGetEntry)
                .build();
    }

    private void doAddEntry(AddEntry msg) {
        log.info("add entry [{}] to list [{}]", msg, this.owner);

        LocalDate date = msg.getDate();
        String value = msg.getValue();

        this.list.store(date, value);
    }

    private void doGetEntry(GetEntry msg) {
        log.info("get entry [{}] from list [{}]", msg, this.owner);

        LocalDate date = msg.getDate();
        Collection<String> tasksForDate = this.list.entriesFor(date);

        getSender().tell(new Entry(tasksForDate), getSelf());
    }
}
