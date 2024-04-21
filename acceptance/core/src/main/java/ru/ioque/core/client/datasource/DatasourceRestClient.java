package ru.ioque.core.client.datasource;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import ru.ioque.core.client.JsonApplicationHttpClient;
import ru.ioque.core.dto.datasource.request.DisableUpdateInstrumentRequest;
import ru.ioque.core.dto.datasource.request.EnableUpdateInstrumentRequest;
import ru.ioque.core.dto.datasource.request.RegisterDatasourceRequest;
import ru.ioque.core.dto.datasource.response.DatasourceResponse;
import ru.ioque.core.dto.datasource.response.InstrumentInListResponse;
import ru.ioque.core.dto.datasource.response.InstrumentResponse;

import java.util.List;
import java.util.UUID;

public class DatasourceRestClient extends JsonApplicationHttpClient {
    public DatasourceRestClient(String apiUrl) {
        super(apiUrl);
    }

    @SneakyThrows
    public void registerDatasource(RegisterDatasourceRequest request) {
        post("/api/datasource", objectMapper.writeValueAsString(request));
    }

    @SneakyThrows
    public void integrateInstruments(UUID datasourceId) {
        post("/api/datasource/" + datasourceId + "/instrument");
    }

    @SneakyThrows
    public void integrateTradingData(UUID datasourceId) {
        post("/api/datasource/" + datasourceId + "/trading-data");
    }

    @SneakyThrows
    public List<DatasourceResponse> getExchanges() {
        return objectMapper.readValue(get("/api/datasource"), new TypeReference<>(){});
    }

    @SneakyThrows
    public DatasourceResponse getExchangeBy(UUID datasourceId) {
        return objectMapper.readValue(get("/api/datasource/" + datasourceId), DatasourceResponse.class);
    }

    @SneakyThrows
    public List<InstrumentInListResponse> getInstruments(UUID datasourceId, String params) {
        String path = "/api/datasource/" + datasourceId + "/instrument" + (params == null || params.isEmpty() ? "" : ("?" + params));
        return objectMapper.readValue(get(path), new TypeReference<>(){});
    }

    @SneakyThrows
    public void enableUpdateInstruments(UUID datasourceId, EnableUpdateInstrumentRequest request) {
        patch("/api/datasource/" + datasourceId + "/enable-update", objectMapper.writeValueAsString(request));
    }

    @SneakyThrows
    public void disableUpdateInstruments(UUID datasourceId, DisableUpdateInstrumentRequest request) {
        patch("/api/datasource/" + datasourceId + "/disable-update", objectMapper.writeValueAsString(request));
    }

    @SneakyThrows
    public InstrumentResponse getInstrumentBy(UUID datasourceId, String ticker) {
        return objectMapper.readValue(get("/api/datasource/" + datasourceId + "/instrument/" + ticker), InstrumentResponse.class);
    }

    @SneakyThrows
    public void runArchiving() {
        post("/api/archive");
    }
}
