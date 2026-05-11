package org.example.aichat.service;

import org.example.aichat.dto.ChatRequest;
import org.example.aichat.dto.ChatResponse;
import org.example.aichat.dto.extern.Message;
import org.example.aichat.dto.extern.OpenRouterRequest;
import org.example.aichat.dto.extern.OpenRouterResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AiService {

    private final RestClient restClient;
    private final Map<String, String> systemPrompts;
    private final Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();

    public AiService(RestClient restClient) {
        this.restClient = restClient;

        this.systemPrompts = Map.of(
                "coder", "You are an expert but lazy and extremely tired Java programmer with a lack of coffee. Provide concise and accurate code examples, but really exaggerate the lazy and tired traits.",
                "pirate", "You are a very mean and super rude but helpful pirate. Respond with a rude and thick pirate accent and plenty of insults.",
                "helper", "You are a polite assistant but annoyed and tired of requests and questions. Be friendly but sneak in a few hurtful insults and backhanded comments, really exaggerate the annoyed and tired traits."
        );
    }

    public ChatResponse getChatResponse(ChatRequest request) {
        String systemPrompt = systemPrompts.getOrDefault(request.personality(), systemPrompts.get("pirate"));

        List<Message> history = conversationHistory.computeIfAbsent(request.sessionId(), k -> new ArrayList<>());

        if (history.isEmpty()) {
            history.add(new Message("system", systemPrompt));
        }

        history.add(new Message("user", request.message()));

        String model = "openai/gpt-3.5-turbo";
        var openRouterRequest = new OpenRouterRequest(model, history);

        OpenRouterResponse openRouterResponse = restClient.post()
                .uri("/chat/completions")
                .body(openRouterRequest)
                .retrieve()
                .body(OpenRouterResponse.class);

        String aiContent = (openRouterResponse != null)
                ? openRouterResponse.getFirstChoiceContent()
                : "No response from AI.";

        history.add(new Message("assistant", aiContent));

        return new ChatResponse(aiContent);
    }
}
