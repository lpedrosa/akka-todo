package com.github.lpedrosa.todo.actor.guardian.message.reply;

import akka.actor.ActorRef;

public class CreateSuccess {

    private final String name;
    private final ActorRef ref;

    public CreateSuccess(String name, ActorRef ref) {
        this.name = name;
        this.ref = ref;
    }

    public String getName() {
        return name;
    }

    public ActorRef getRef() {
        return ref;
    }

}
