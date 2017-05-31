package com.github.lpedrosa.todo.api;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.pattern.PatternsCS;
import akka.util.Timeout;
import com.github.lpedrosa.todo.actor.Todos;
import com.github.lpedrosa.todo.actor.server.message.reply.Entry;
import com.github.lpedrosa.todo.actor.server.message.request.AddEntry;
import com.github.lpedrosa.todo.actor.server.message.request.GetEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/todo")
@Produces(MediaType.APPLICATION_JSON)
public class TodoResource {
    private static final Logger LOG = LoggerFactory.getLogger(TodoResource.class);
    private static final Timeout DEFAULT_TIMEOUT = new Timeout(5, TimeUnit.SECONDS);

    private final Todos todos;

    public TodoResource(Todos todos) {
        this.todos = todos;
    }

    @GET
    public void retrieveEntries(@Suspended AsyncResponse asyncResponse,
                                @QueryParam("owner") String owner,
                                @QueryParam("date") String dateString) {

        // FIXME the String to LocalDate conversion should be somewhere else
        final LocalDate date;
        try {
            date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            LOG.warn("Failed to parse date [{}] while retrieving entries.", dateString);
            Response r = Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("The given date " + dateString + " does not respect the following format yyyy-MM-dd"))
                    .build();
            asyncResponse.resume(r);
            return;
        }

        CompletableFuture<ActorRef> todoList = todos.listFor(owner);

        CompletableFuture<Todo> todo = todoList.thenCompose(todoServer -> todoEntriesForDate(todoServer, date))
                .thenApply(entries -> new Todo(owner, date, entries));

        todo.thenApply(asyncResponse::resume)
                .exceptionally(asyncResponse::resume); // not treating the exceptions here
    }


    private static CompletableFuture<Collection<String>> todoEntriesForDate(ActorRef todoList, LocalDate date) {
        GetEntry getEntry = new GetEntry(date);

        CompletableFuture<Entry> entry = PatternsCS.ask(todoList, getEntry, DEFAULT_TIMEOUT)
                .toCompletableFuture()
                .thenCompose(TodoResource::castEntry);

        return entry.thenApply(Entry::getTasks);
    }

    private static CompletableFuture<Entry> castEntry(Object o) {
        CompletableFuture<Entry> result = new CompletableFuture<>();

        try {
            result.complete((Entry) o);
        } catch (ClassCastException ex) {
            result.completeExceptionally(ex);
        }

        return result;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void retrieveEntries(@Suspended AsyncResponse asyncResponse, TodoEntry entry) {
        CompletableFuture<ActorRef> todoList = todos.listFor(entry.getOwner());

        todoList.thenAccept(todoServer -> newEntry(todoServer, entry))
                .thenApply(ignored -> asyncResponse.resume(Response.ok().build()))
                .exceptionally(asyncResponse::resume);
    }

    private static void newEntry(ActorRef todoServer, TodoEntry entry) {
        AddEntry getEntry = new AddEntry(entry.getDate(), entry.getEntry());

        todoServer.tell(getEntry, ActorRef.noSender());
    }

}
