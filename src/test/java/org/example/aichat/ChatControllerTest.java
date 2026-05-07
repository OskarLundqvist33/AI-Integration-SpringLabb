package org.example.aichat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.aichat.controller.ChatController;
import org.example.aichat.dto.ChatRequest;
import org.example.aichat.dto.ChatResponse;
import org.example.aichat.service.AiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AiService aiService;

    @Test
    void whenValidRequest_thenReturns200AndCorrectResponse() throws Exception {
        var request = new ChatRequest("coder", "Explain recursion", "test-session-123");
        var expectedResponse = new ChatResponse("Recursion is just recursion.");

        when(aiService.getChatResponse(any(ChatRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(expectedResponse.response()));
    }
}