package com.github.lpedrosa.todo.actor.guardian;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import com.github.lpedrosa.todo.actor.guardian.message.reply.CreateFailed;
import com.github.lpedrosa.todo.actor.guardian.message.request.Create;

public class TodoGuardian extends AbstractActor {

    private final SupervisorStrategy strategy;

    private static Props props(SupervisorStrategy strategy) {
        return Props.create(TodoGuardian.class, () -> new TodoGuardian(strategy));
    }

    private TodoGuardian(SupervisorStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return this.strategy;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Create.class, this::doCreate)
                .build();
    }

    private void doCreate(Create msg) {
        Props props = msg.getProps();
        String name = msg.getName();

        final ActorRef child;
        try {
            child = getContext()
                    .findChild(name)
                    .orElseGet(() -> getContext().actorOf(props, name));
        } catch (RuntimeException ex) {
            getSender().tell(new CreateFailed(ex), getSelf());
            return;
        }

        getSender().tell(child, getSelf());
    }

}
