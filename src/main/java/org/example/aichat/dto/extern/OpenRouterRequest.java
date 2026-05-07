package org.example.aichat.dto.extern;

import java.util.List;

public record OpenRouterRequest(
        String model,
        List<Message> messages
) {}
