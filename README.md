# Lab 1: AI-Integrated Spring Boot Service

## Introduction

In this lab, you will build a middleware service using Spring Boot. The application will act as a bridge between an end-user and a Large Language Model (LLM). Instead of the user communicating directly with the AI, the requests will go through your application. This allows you to control the AI's personality (via a system prompt), manage the conversation context (memory), and secure the communication.

---

### 1. Architecture

The application must expose a single REST endpoint: `POST /api/v1/chat`.

The request body should contain the user's message and a choice of "personality".

**Example Request Body:**
```json
{
  "personality": "coder",
  "message": "How do I write a for-loop in Java?",
  "sessionId": "user-123-abc"
}
```

| Fält        | Typ    | Beskrivning                                               | Exempelvärden        | Obligatorisk                                          |
|-------------|--------|-----------------------------------------------------------|----------------------|-------------------------------------------------------|
| personality | String | Bestämmer vilken system-prompt som ska användas.          | helper, pirate,coder | Ja                                                    |
| message     | String | Själva frågan från slutanvändaren.                        | "Vad ärpolymorfism?" | Ja                                                    |
| sessionId   | String | Används för att hålla isär olika chatt-historiker(minne). | "uuid-string-123"    | Nej, Används för att kunna fortsätta en konversation. |


   • Applikationen mappar valet av personlighet till en specifik System Prompt.

   • RestClient: Använd Spring RestClient för att skicka en begäran till den externa
   modellen (OpenRouter eller LM Studio). REST Clients :: Spring Framework


### 2. Minne och Kontext
   För att AI:n ska förstå uppföljningsfrågor måste du skicka med tidigare historik i anropet.
   • Implementera en enkel in-memory lagring (t.ex. en Map eller List) som håller reda på de
   senaste anropen per session eller användare.


### 3. Säkerhet och Konfiguration (VG)
   • API-nycklar: Inga nycklar får hårdkodas. Använd miljövariabler (.env filer eller
   systemvariabler) som läses in via @Value eller @ConfigurationProperties.
   • OpenAPI/Swagger: Implementera springdoc-openapi så att ditt eget API
   dokumenteras automatiskt på /swagger-ui.html.


### 4. Resiliens och Felhantering (VG)
   Nätverksanrop mot AI-modeller kan ofta misslyckas eller ta lång tid.
   • Retry & Backoff: Om API-anropet misslyckas (t.ex. statuskod 429 eller 503), ska
   applikationen automatiskt försöka igen med en exponentiell fördröjning.
   • Global Exception Handler: Skapa en @ControllerAdvice som snyggt returnerar
   felmeddelanden till användaren om AI-tjänsten är nere. Hur hanterar vi fel från ett rest api?
   Best Practices for REST API Error Handling | Baeldung


### 5. Testning
   Hur skriver vi tester för vår applikation? Vi vill kunna testa våra rest endpoints och då kan vi mocka
   vår service klass som anropar externt api alternativt använda ett mockat api som vi kan anropa.
   Underlättar testning av tex retry då vi kan generera fel på ett förutsägbart sätt för testet.
   WireMock Java - API Mocking for Java and JVM | WireMock
