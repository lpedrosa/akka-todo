package com.github.lpedrosa.todo;

import akka.actor.ActorSystem;
import com.github.lpedrosa.todo.actor.Todos;
import com.github.lpedrosa.todo.api.JsonMappingExceptionMapper;
import com.github.lpedrosa.todo.api.TodoResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TodoApplication extends Application<TodoConfiguration> {

    private static final Logger LOG = LoggerFactory.getLogger(TodoApplication.class);

    public static void main(String[] args) throws Exception {
        new TodoApplication().run(args);
    }

    @Override
    public void run(TodoConfiguration configuration, Environment environment) throws Exception {
        // register logger for lifecycle failures
        environment.lifecycle().addLifeCycleListener(createLoggingListener());

        ActorSystem system = configuration.getAkkaConfig().build(environment);
        Todos todos = new Todos(system);

        TodoResource todoResource = new TodoResource(todos);
        environment.jersey().register(todoResource);

        // exception mappers
        // NOTE: this overrides default json mapping exception mapper
        environment.jersey().register(new JsonMappingExceptionMapper());
    }

    private LifeCycle.Listener createLoggingListener() {
        return new AbstractLifeCycle.AbstractLifeCycleListener() {
            @Override
            public void lifeCycleFailure(LifeCycle event, Throwable cause) {
                LOG.error("An error occurred while shutting down the application:", cause);
            }
        };
    }

}
