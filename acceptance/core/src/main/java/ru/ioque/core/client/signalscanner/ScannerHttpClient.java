package ru.ioque.core.client.signalscanner;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import ru.ioque.core.client.JsonHttpClient;
import ru.ioque.core.dto.scanner.request.CreateScannerRequest;
import ru.ioque.core.dto.scanner.request.UpdateScannerRequest;
import ru.ioque.core.dto.scanner.response.SignalScannerInListResponse;
import ru.ioque.core.dto.scanner.response.SignalScannerResponse;

import java.util.List;
import java.util.UUID;

public class ScannerHttpClient extends JsonHttpClient {
    public ScannerHttpClient(String apiUrl) {
        super(apiUrl);
    }

    @SneakyThrows
    public List<SignalScannerInListResponse> getDataScanners() {
        return objectMapper.readValue(get("/api/scanner"), new TypeReference<>(){});
    }

    @SneakyThrows
    public SignalScannerResponse getSignalScannerBy(UUID id) {
        return objectMapper.readValue(get("/api/scanner/" + id), SignalScannerResponse.class);
    }

    @SneakyThrows
    public void createScanner(CreateScannerRequest request) {
        post("/api/scanner", objectMapper.writeValueAsString(request));
    }

    @SneakyThrows
    public void updateScanner(UUID scannerId, UpdateScannerRequest request) {
        patch("/api/scanner/" + scannerId, objectMapper.writeValueAsString(request));
    }

    public void removeScanner(UUID scannerId) {
        delete("/api/scanner/" + scannerId);
    }

    public void activateScanner(UUID scannerId) {
        patch("/api/scanner/" + scannerId + "/activate");
    }

    public void deactivateScanner(UUID scannerId) {
        patch("/api/scanner/" + scannerId + "/deactivate");
    }
}
