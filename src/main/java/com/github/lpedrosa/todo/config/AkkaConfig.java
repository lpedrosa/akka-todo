package com.github.lpedrosa.todo.config;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import akka.actor.ActorSystem;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import org.hibernate.validator.constraints.NotEmpty;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class AkkaConfig {

    @NotEmpty
    private String sytemName;

    private String logLevel;

    @JsonCreator
    public AkkaConfig(@JsonProperty("systemName") String sytemName,
                      @JsonProperty("logLevel") String logLevel) {
        this.sytemName = sytemName;
        this.logLevel = logLevel;
    }

    @JsonProperty("systemName")
    public String getSytemName() {
        return this.sytemName;
    }

    @JsonProperty("logLevel")
    public String getLogLevel() {
        return this.logLevel;
    }

    public ActorSystem build(Environment environment) {
        ActorSystem system = ActorSystem.create(this.sytemName, buildConfig(this.logLevel));

        // make sure the system is terminated gracefully when dropwizard terminates
        environment.lifecycle().manage(manageActorSystem(system));

        return system;
    }

    private Config buildConfig(String logLevel) {
        Map<String, Object> logConfig = ImmutableMap.<String, Object>builder()
                .put("akka.loglevel", parseLogLevel(logLevel).orElse("INFO"))
                .put("akka.loggers", ImmutableList.of("akka.event.slf4j.Slf4jLogger"))
                .put("akka.logging-filter", "akka.event.slf4j.Slf4jLoggingFilter")
                .build();

        return ConfigFactory.parseMap(logConfig);
    }

    private static final Set<String> validLevels = ImmutableSet.of("INFO", "DEBUG", "ERROR");

    private static Optional<String> parseLogLevel(String logLevel) {
        if (validLevels.contains(logLevel.toUpperCase())) {
            return Optional.of(logLevel);
        }

        return Optional.empty();
    }

    private static Managed manageActorSystem(ActorSystem system) {
        return new Managed() {
            @Override
            public void start() throws Exception {
                // do nothing
            }

            @Override
            public void stop() throws Exception {
                final Future<?> terminate = system.terminate();

                // wait for actor system to terminate
                // this method will throw if it hits the timeout
                Await.result(terminate, Duration.apply(1, TimeUnit.SECONDS));
            }
        };
    }

}
