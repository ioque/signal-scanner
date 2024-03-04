package ru.ioque.acceptance.adapters.client.signalscanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.ioque.acceptance.adapters.client.RestTemplateFacade;
import ru.ioque.acceptance.adapters.client.signalscanner.request.AddSignalScannerRequest;
import ru.ioque.acceptance.adapters.client.signalscanner.response.Signal;
import ru.ioque.acceptance.adapters.client.signalscanner.response.SignalScannerInList;
import ru.ioque.acceptance.domain.scanner.SignalScanner;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class SignalScannerRestClient {
    RestTemplateFacade restTemplateFacade;
    ObjectMapper objectMapper;

    @SneakyThrows
    public List<SignalScannerInList> getDataScanners() {
        return objectMapper.readValue(restTemplateFacade.get("/api/v1/signal-scanner", String.class), new TypeReference<>(){});
    }

    public SignalScanner getSignalScannerBy(UUID id) {
        return restTemplateFacade.get("/api/v1/signal-scanner/" + id, SignalScanner.class);
    }

    @SneakyThrows
    public void saveDataScannerConfig(AddSignalScannerRequest request) {
        restTemplateFacade.post("/api/v1/signal-scanner", objectMapper.writeValueAsString(request));
    }

    @SneakyThrows
    public void runScanning() {
        restTemplateFacade.post("/api/v1/signal-scanner/run", null);
    }

    @SneakyThrows
    public List<Signal> getSignals() {
        return objectMapper.readValue(restTemplateFacade.get("/api/v1/signals", String.class), new TypeReference<>(){});
    }
}
