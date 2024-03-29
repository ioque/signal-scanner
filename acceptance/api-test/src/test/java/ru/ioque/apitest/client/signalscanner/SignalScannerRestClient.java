package ru.ioque.apitest.client.signalscanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.ioque.apitest.client.RestTemplateFacade;
import ru.ioque.apitest.client.signalscanner.request.AddSignalScannerRequest;
import ru.ioque.apitest.client.signalscanner.response.SignalScannerInList;
import ru.ioque.apitest.dto.scanner.SignalScanner;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class SignalScannerRestClient {
    RestTemplateFacade restTemplateFacade;
    ObjectMapper objectMapper;

    @SneakyThrows
    public List<SignalScannerInList> getDataScanners() {
        return objectMapper.readValue(restTemplateFacade.get("/api/signal-scanner", String.class), new TypeReference<>(){});
    }

    public SignalScanner getSignalScannerBy(UUID id) {
        return restTemplateFacade.get("/api/signal-scanner/" + id, SignalScanner.class);
    }

    @SneakyThrows
    public void saveDataScannerConfig(AddSignalScannerRequest request) {
        restTemplateFacade.post("/api/signal-scanner", objectMapper.writeValueAsString(request));
    }

    @SneakyThrows
    public void runScanning() {
        restTemplateFacade.post("/api/signal-scanner/run", null);
    }
}
