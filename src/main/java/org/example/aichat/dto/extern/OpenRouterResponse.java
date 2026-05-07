package org.example.aichat.dto.extern;

import java.util.List;

public record OpenRouterResponse(List<Choice> choices) {

    public record Choice(Message message) {}

    public String getFirstChoiceContent() {
        if (choices == null || choices.isEmpty() || choices.get(0).message() == null) {
            return "No response content received from AI.";
        }
        return choices.get(0).message().content();
    }
}
