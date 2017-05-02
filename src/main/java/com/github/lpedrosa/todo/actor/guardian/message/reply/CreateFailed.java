package com.github.lpedrosa.todo.actor.guardian.message.reply;

public class CreateFailed {

    public final Throwable reason;

    public CreateFailed(Throwable reason) {
        this.reason = reason;
    }

    public Throwable getReason() {
        return reason;
    }

}
