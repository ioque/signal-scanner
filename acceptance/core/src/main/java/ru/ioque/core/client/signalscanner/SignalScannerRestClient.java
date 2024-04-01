package ru.ioque.core.client.signalscanner;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import ru.ioque.core.client.BaseClient;
import ru.ioque.core.dto.scanner.request.AddSignalScannerRequest;
import ru.ioque.core.dto.scanner.response.SignalScannerInListResponse;
import ru.ioque.core.dto.scanner.response.SignalScannerResponse;

import java.util.List;
import java.util.UUID;

public class SignalScannerRestClient extends BaseClient {
    public SignalScannerRestClient(String apiHost) {
        super(apiHost);
    }

    @SneakyThrows
    public List<SignalScannerInListResponse> getDataScanners() {
        return objectMapper.readValue(get("/api/signal-scanner"), new TypeReference<>(){});
    }

    @SneakyThrows
    public SignalScannerResponse getSignalScannerBy(UUID id) {
        return objectMapper.readValue(get("/api/signal-scanner/" + id), SignalScannerResponse.class);
    }

    @SneakyThrows
    public void saveDataScannerConfig(AddSignalScannerRequest request) {
        post("/api/signal-scanner", objectMapper.writeValueAsString(request));
    }
}
