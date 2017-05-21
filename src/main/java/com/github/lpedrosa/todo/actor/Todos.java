package com.github.lpedrosa.todo.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.pattern.PatternsCS;
import com.github.lpedrosa.todo.actor.guardian.TodoGuardian;
import com.github.lpedrosa.todo.actor.guardian.message.reply.CreateFailed;
import com.github.lpedrosa.todo.actor.guardian.message.reply.CreateSuccess;
import com.github.lpedrosa.todo.actor.guardian.message.request.Create;
import com.github.lpedrosa.todo.actor.server.TodoServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class Todos {

    private static final Logger LOG = LoggerFactory.getLogger(Todos.class);
    private static final long DEFAULT_TIMEOUT = 5000;
    
    private final ActorRef guardian;

    public Todos(ActorSystem system) {
        this.guardian = createGuardian(system);
    }

    private static ActorRef createGuardian(ActorSystem system) {
        SupervisorStrategy strategy = SupervisorStrategy.stoppingStrategy();
        Props guardianProps = TodoGuardian.props(strategy);
        return system.actorOf(guardianProps, "todoGuardian");
    }

    public CompletableFuture<Optional<ActorRef>> listFor(String name) {
        Props props = TodoServer.props(name);
        Create create = new Create(props, "todoList-"+name);

        CompletionStage<Object> reply = PatternsCS.ask(guardian, create, DEFAULT_TIMEOUT);

        return reply.thenApply(msg -> handleCreateReply(msg, name)).toCompletableFuture();
    }

    private static Optional<ActorRef> handleCreateReply(Object msg, String name) {
        if (msg instanceof CreateSuccess) {
            CreateSuccess success = (CreateSuccess) msg;
            return Optional.of(success.getRef());
        } else if (msg instanceof CreateFailed) {
            CreateFailed reply = (CreateFailed) msg;
            LOG.error("Failed to retrieve todo server for name: {}", name, reply.getReason());
            return Optional.empty();
        } else {
            throw new IllegalStateException("received unknown reply!");
        }
    }

}
