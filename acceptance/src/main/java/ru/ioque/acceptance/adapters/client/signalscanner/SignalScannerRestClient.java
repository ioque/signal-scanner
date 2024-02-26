package ru.ioque.acceptance.adapters.client.signalscanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.ioque.acceptance.adapters.client.RestTemplateFacade;
import ru.ioque.acceptance.adapters.client.signalscanner.request.AddSignalScannerRequest;
import ru.ioque.acceptance.adapters.client.signalscanner.response.SignalScannerInList;

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

    public SignalScannerInList getDataScannerBy(UUID id) {
        return restTemplateFacade.get("/api/v1/signal-scanner/" + id, SignalScannerInList.class);
    }

    @SneakyThrows
    public void saveDataScannerConfig(AddSignalScannerRequest request) {
        restTemplateFacade.post("/api/v1/signal-scanner", objectMapper.writeValueAsString(request));
    }
}
