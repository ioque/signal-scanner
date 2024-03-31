package ru.ioque.apitest.client.exchange;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.ioque.apitest.client.RestTemplateFacade;
import ru.ioque.apitest.client.exchange.request.DisableUpdateInstrumentRequest;
import ru.ioque.apitest.client.exchange.request.EnableUpdateInstrumentRequest;
import ru.ioque.apitest.dto.exchange.ExchangeResponse;
import ru.ioque.apitest.dto.exchange.InstrumentInListResponse;
import ru.ioque.apitest.dto.exchange.InstrumentResponse;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ExchangeRestClient {
    RestTemplateFacade restTemplateFacade;
    ObjectMapper objectMapper;

    public void synchronizeWithDataSource() {
        restTemplateFacade.post("/api/integrate", null);
    }

    @SneakyThrows
    public void integrateTradingData() {
        restTemplateFacade.post("/api/daily-integrate", null);
    }

    public ExchangeResponse getExchange() {
        return restTemplateFacade.get("/api/exchange", ExchangeResponse.class);
    }

    @SneakyThrows
    public List<InstrumentInListResponse> getInstruments(String params) {
        String path = "/api/instruments" + (params == null || params.isEmpty() ? "" : ("?" + params));
        return objectMapper.readValue(restTemplateFacade.get(path, String.class), new TypeReference<>(){});
    }

    @SneakyThrows
    public void enableUpdateInstruments(EnableUpdateInstrumentRequest request) {
        restTemplateFacade.patch("/api/enable-update", objectMapper.writeValueAsString(request), String.class);
    }

    @SneakyThrows
    public void disableUpdateInstruments(DisableUpdateInstrumentRequest request) {
        restTemplateFacade.patch("/api/disable-update", objectMapper.writeValueAsString(request), String.class);
    }

    public InstrumentResponse getInstrumentBy(UUID id) {
        return restTemplateFacade.get("/api/instruments/" + id, InstrumentResponse.class);
    }

    public void clearIntradayValue() {
        restTemplateFacade.delete("/api/intraday-value");
    }

    public void runArchiving() {
        restTemplateFacade.post("/api/archiving", null);
    }
}
