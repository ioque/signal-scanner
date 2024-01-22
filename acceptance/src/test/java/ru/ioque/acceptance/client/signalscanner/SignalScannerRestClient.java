package ru.ioque.acceptance.client.signalscanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.ioque.acceptance.client.RestTemplateFacade;
import ru.ioque.acceptance.client.signalscanner.response.FinancialInstrumentInList;
import ru.ioque.acceptance.client.signalscanner.response.FinancialInstrument;
import ru.ioque.acceptance.client.signalscanner.request.DataScannerConfigRequest;
import ru.ioque.acceptance.client.signalscanner.response.DataScannerInListResponse;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class SignalScannerRestClient {
    RestTemplateFacade restTemplateFacade;
    ObjectMapper objectMapper;

    @SneakyThrows
    public List<FinancialInstrumentInList> getInstruments() {
        return objectMapper.readValue(restTemplateFacade.get("/financial-instruments", String.class), new TypeReference<>(){});
    }

    @SneakyThrows
    public FinancialInstrument getInstrumentBy(UUID id) {
        return restTemplateFacade.get("/financial-instruments/" + id, FinancialInstrument.class);
    }

    @SneakyThrows
    public List<DataScannerInListResponse> getDataScanners() {
        return objectMapper.readValue(restTemplateFacade.get("/data-scanner", String.class), new TypeReference<>(){});
    }

    public DataScannerInListResponse getDataScannerBy(UUID id) {
        return restTemplateFacade.get("/data-scanner/" + id, DataScannerInListResponse.class);
    }

    @SneakyThrows
    public void saveDataScannerConfig(DataScannerConfigRequest request) {
        restTemplateFacade.post("/data-scanner", objectMapper.writeValueAsString(request));
    }
}
