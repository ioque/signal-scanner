package ru.ioque.acceptance.client.signalscanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.ioque.acceptance.client.RestTemplateFacade;
import ru.ioque.acceptance.client.signalscanner.request.AddSignalScannerRequest;
import ru.ioque.acceptance.client.signalscanner.response.DataScannerInListResponse;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class SignalScannerRestClient {
    RestTemplateFacade restTemplateFacade;
    ObjectMapper objectMapper;

    @SneakyThrows
    public List<DataScannerInListResponse> getDataScanners() {
        return objectMapper.readValue(restTemplateFacade.get("/signal-scanner", String.class), new TypeReference<>(){});
    }

    public DataScannerInListResponse getDataScannerBy(UUID id) {
        return restTemplateFacade.get("/signal-scanner/" + id, DataScannerInListResponse.class);
    }

    @SneakyThrows
    public void saveDataScannerConfig(AddSignalScannerRequest request) {
        restTemplateFacade.post("/signal-scanner", objectMapper.writeValueAsString(request));
    }
}
