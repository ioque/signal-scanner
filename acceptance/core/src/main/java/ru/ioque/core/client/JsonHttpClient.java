package ru.ioque.core.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class JsonHttpClient {
    protected final String apiUrl;
    protected final HttpClient client = HttpClient.newHttpClient();
    protected final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @SneakyThrows
    protected String get(String path) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(apiUrl + path))
            .header("Content-Type", "application/json")
            .GET()
            .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        checkError(response);
        return response.body();
    }

    @SneakyThrows
    protected void post(String path) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(apiUrl + path))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();
        checkError(client.send(request, BodyHandlers.ofString()));
    }

    @SneakyThrows
    protected void post(String path, String body) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(apiUrl + path))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        checkError(client.send(request, BodyHandlers.ofString()));
    }

    @SneakyThrows
    protected void delete(String path) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(apiUrl + path))
            .header("Content-Type", "application/json")
            .DELETE()
            .build();
        checkError(client.send(request, BodyHandlers.ofString()));
    }

    @SneakyThrows
    protected void patch(String path, String body) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(apiUrl + path))
            .header("Content-Type", "application/json")
            .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
            .build();
        checkError(client.send(request, BodyHandlers.ofString()));
    }

    private void checkError(HttpResponse<String> response) throws JsonProcessingException {
        if(response.statusCode() == 400) {
            List<String> errors = new ArrayList<>();
            JsonNode jsonNode = objectMapper.readTree(response.body()).get("errors");
            if (Objects.nonNull(jsonNode) && jsonNode.isArray() && !jsonNode.isEmpty()) {
                for (int i = 0; i < jsonNode.size(); i++) {
                    errors.add(jsonNode.get(0).asText());
                }
            }
            throw new RuntimeException(errors.toString());
        }
        if (response.statusCode() != 200) {
            String message = objectMapper.readTree(response.body()).get("message").asText();
            throw new RuntimeException(message);
        }
    }
}
