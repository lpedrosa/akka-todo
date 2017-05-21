package com.github.lpedrosa.todo.actor.guardian.message.reply;

public class CreateFailed {

    public final Exception reason;

    public CreateFailed(Exception reason) {
        this.reason = reason;
    }

    public Exception getReason() {
        return reason;
    }

}
