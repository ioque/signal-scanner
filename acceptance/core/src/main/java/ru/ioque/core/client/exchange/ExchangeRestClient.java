package ru.ioque.core.client.exchange;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import ru.ioque.core.client.BaseClient;
import ru.ioque.core.dto.exchange.request.DisableUpdateInstrumentRequest;
import ru.ioque.core.dto.exchange.request.EnableUpdateInstrumentRequest;
import ru.ioque.core.dto.exchange.request.RegisterDatasourceRequest;
import ru.ioque.core.dto.exchange.response.ExchangeResponse;
import ru.ioque.core.dto.exchange.response.InstrumentInListResponse;
import ru.ioque.core.dto.exchange.response.InstrumentResponse;

import java.util.List;
import java.util.UUID;

public class ExchangeRestClient extends BaseClient {
    public ExchangeRestClient(String baseUrl) {
        super(baseUrl);
    }

    @SneakyThrows
    public void registerDatasource(RegisterDatasourceRequest request) {
        post("/api/datasource", objectMapper.writeValueAsString(request));
    }

    @SneakyThrows
    public void synchronizeWithDataSource() {
        post("/api/integrate");
    }

    @SneakyThrows
    public void integrateTradingData() {
        post("/api/daily-integrate");
    }

    @SneakyThrows
    public ExchangeResponse getExchange() {
        return objectMapper.readValue(get("/api/exchange"), ExchangeResponse.class);
    }

    @SneakyThrows
    public List<InstrumentInListResponse> getInstruments(String params) {
        String path = "/api/instruments" + (params == null || params.isEmpty() ? "" : ("?" + params));
        return objectMapper.readValue(get(path), new TypeReference<>(){});
    }

    @SneakyThrows
    public void enableUpdateInstruments(EnableUpdateInstrumentRequest request) {
        patch("/api/enable-update", objectMapper.writeValueAsString(request));
    }

    @SneakyThrows
    public void disableUpdateInstruments(DisableUpdateInstrumentRequest request) {
        patch("/api/disable-update", objectMapper.writeValueAsString(request));
    }

    @SneakyThrows
    public InstrumentResponse getInstrumentBy(UUID id) {
        return objectMapper.readValue(get("/api/instruments/" + id), InstrumentResponse.class);
    }

    @SneakyThrows
    public void runArchiving() {
        post("/api/archiving");
    }
}
