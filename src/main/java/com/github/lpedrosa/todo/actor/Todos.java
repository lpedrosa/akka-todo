package com.github.lpedrosa.todo.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.pattern.PatternsCS;
import com.github.lpedrosa.todo.actor.guardian.TodoGuardian;
import com.github.lpedrosa.todo.actor.guardian.message.reply.CreateFailed;
import com.github.lpedrosa.todo.actor.guardian.message.request.Create;
import com.github.lpedrosa.todo.actor.server.TodoServer;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class Todos {

    private static final long DEFAULT_TIMEOUT = 5000;
    
    private final ActorSystem system;
    private final ActorRef guardian;

    public Todos(ActorSystem system) {
        this.system = system;
        Props guardianProps = TodoGuardian.props(SupervisorStrategy.defaultStrategy());
        this.guardian = system.actorOf(guardianProps, "todoGuardian");
    }

    public ActorRef listFor(String name) throws Throwable {
        Props props = TodoServer.props(name);
        Create create = new Create(props, "todoList-"+name);

        CompletionStage<Object> reply = PatternsCS.ask(guardian, 
                create, 
                DEFAULT_TIMEOUT);

        final ActorRef ref;
        try {
            ref = reply.thenApply(this::getRefOrError)
                .toCompletableFuture()
                .get();
        } catch (ExecutionException e) {
            // unwrap execution exception
            throw e.getCause();
        }

        return ref;
    }

    private ActorRef getRefOrError(Object msg) {
        if (msg instanceof ActorRef) {
            return (ActorRef) msg;
        } else if (msg instanceof CreateFailed) {
            CreateFailed reply = (CreateFailed)msg;
            throw new RuntimeException(reply.getReason());
        } else {
            throw new IllegalStateException("received unknown reply!");
        }
    }

}
