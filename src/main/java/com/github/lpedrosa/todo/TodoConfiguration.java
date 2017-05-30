package com.github.lpedrosa.todo;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.lpedrosa.todo.config.AkkaConfig;
import io.dropwizard.Configuration;

public class TodoConfiguration extends Configuration {

    @Valid
    private AkkaConfig akkaConfig;

    @JsonCreator
    public TodoConfiguration(@JsonProperty("akka") AkkaConfig akkaConfig) {
        this.akkaConfig = akkaConfig;
    }

    @JsonProperty("akka")
    public AkkaConfig getAkkaConfig() {
        return this.akkaConfig;
    }

}
