package ru.ioque.core.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

@RequiredArgsConstructor
public class JsonApplicationHttpClient {
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
        return client.send(request, BodyHandlers.ofString()).body();
    }

    @SneakyThrows
    protected void post(String path) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(apiUrl + path))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();
        client.send(request, BodyHandlers.ofString());
    }

    @SneakyThrows
    protected void post(String path, String body) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(apiUrl + path))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        client.send(request, BodyHandlers.ofString());
    }

    @SneakyThrows
    protected void patch(String path, String body) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(apiUrl + path))
            .header("Content-Type", "application/json")
            .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
            .build();
        client.send(request, BodyHandlers.ofString());
    }

    @SneakyThrows
    protected void delete(String path) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(apiUrl + path))
            .header("Content-Type", "application/json")
            .DELETE()
            .build();
        client.send(request, BodyHandlers.ofString());
    }
}
