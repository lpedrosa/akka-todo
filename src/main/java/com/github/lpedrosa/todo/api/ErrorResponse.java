package com.github.lpedrosa.todo.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {

    private String reason;

    public ErrorResponse(String reason) {
        this.reason = reason;
    }

    @JsonProperty("reason")
    public String getReason() {
        return reason;
    }

}
