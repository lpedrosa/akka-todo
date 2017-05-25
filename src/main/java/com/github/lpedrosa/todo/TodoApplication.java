package com.github.lpedrosa.todo;

import akka.actor.ActorSystem;
import com.github.lpedrosa.todo.actor.Todos;
import com.github.lpedrosa.todo.api.JsonMappingExceptionMapper;
import com.github.lpedrosa.todo.api.TodoResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class TodoApplication extends Application<TodoConfiguration> {

    public static void main(String[] args) throws Exception {
        new TodoApplication().run(args);
    }

    @Override
    public void run(TodoConfiguration configuration, Environment environment) throws Exception {
        ActorSystem system = ActorSystem.create("todos");
        Todos todos = new Todos(system);

        TodoResource todoResource = new TodoResource(todos);
        environment.jersey().register(todoResource);

        // exception mappers
        // NOTE: this overrides default json mapping exception mapper
        environment.jersey().register(new JsonMappingExceptionMapper());
    }
}
