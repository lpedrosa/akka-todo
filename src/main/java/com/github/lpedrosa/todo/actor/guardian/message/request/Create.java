package com.github.lpedrosa.todo.actor.guardian.message.request;

import akka.actor.Props;

public class Create {

    private final Props props;
    private final String name;

    public Create(Props props, String name) {
        this.props = props;
        this.name = name;
    }

    public Props getProps() {
        return props;
    }

    public String getName() {
        return name;
    }

}
