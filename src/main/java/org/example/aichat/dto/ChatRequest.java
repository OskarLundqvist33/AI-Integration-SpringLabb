package org.example.aichat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChatRequest(
        String personality,
        String message,
        @JsonProperty("sessionId") String sessionId
) {}
