package org.example.aichat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.core.Options;
import org.example.aichat.dto.ChatRequest;
import org.example.aichat.dto.ChatResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static WireMockServer wireMockServer;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("llm.api.url", wireMockServer::baseUrl);
    }

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .dynamicPort()
                        .useChunkedTransferEncoding(Options.ChunkedEncodingPolicy.NEVER)
        );
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @Test
    void whenChatting_FullApplicationFlowWorks() {
        String expectedAiResponse = "Mocked response from program here";
        String wiremockResponseJson = """
                {
                    "choices": [
                        {
                            "message": {
                                "role": "assistant",
                                "content": "%s"
                            }
                        }
                    ]
                }
                """.formatted(expectedAiResponse);

        wireMockServer.stubFor(post(urlEqualTo("/chat/completions"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(wiremockResponseJson)));

        var request = new ChatRequest("pirate", "Tell me a joke about pirates", "integration-test-1");

        ResponseEntity<ChatResponse> responseEntity = testRestTemplate.postForEntity("/api/v1/chat", request, ChatResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().response()).isEqualTo(expectedAiResponse);

        wireMockServer.verify(1, postRequestedFor(urlEqualTo("/chat/completions"))
                .withRequestBody(containing("\"role\":\"user\","))
                .withRequestBody(containing("\"content\":\"Tell me a joke about pirates\"")));
    }
}
