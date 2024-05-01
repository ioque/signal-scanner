package ru.ioque.core.client.risk;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import ru.ioque.core.client.JsonHttpClient;
import ru.ioque.core.dto.risk.response.EmulatedPositionResponse;

import java.util.List;

public class RiskManagerHttpClient extends JsonHttpClient {
    public RiskManagerHttpClient(String apiUrl) {
        super(apiUrl);
    }

    @SneakyThrows
    public List<EmulatedPositionResponse> getEmulatedPositions() {
        return objectMapper.readValue(get("/api/emulated-position"), new TypeReference<>(){});
    }
}
