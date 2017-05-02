package com.github.lpedrosa.todo.actor.server;

import java.time.LocalDate;
import java.util.Collection;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.github.lpedrosa.todo.actor.server.message.reply.Entry;
import com.github.lpedrosa.todo.actor.server.message.request.AddEntry;
import com.github.lpedrosa.todo.actor.server.message.request.GetEntry;
import com.github.lpedrosa.todo.domain.TodoList;

public class TodoServer extends AbstractActor {

    private final TodoList list;

    public static Props props(String owner) {
        return Props.create(TodoServer.class, () -> new TodoServer(owner));
    }

    private TodoServer(String owner) {
        this.list = new TodoList(owner);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AddEntry.class, this::doAddEntry)
                .match(GetEntry.class, this::doGetEntry)
                .build();
    }

    public void doAddEntry(AddEntry msg) {
        LocalDate date = msg.getDate();
        String value = msg.getValue();

        this.list.storeEntry(date, value);
    }

    public void doGetEntry(GetEntry msg) {
        LocalDate date = msg.getDate();

        Collection<String> tasksForDate = this.list.retrieveEntries(date);

        getSender().tell(new Entry(tasksForDate), getSelf());
    }
}
