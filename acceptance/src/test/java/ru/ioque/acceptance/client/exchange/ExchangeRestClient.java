package ru.ioque.acceptance.client.exchange;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.ioque.acceptance.client.RestTemplateFacade;
import ru.ioque.acceptance.client.exchange.request.EnableUpdateInstrumentRequest;
import ru.ioque.acceptance.domain.exchange.Exchange;
import ru.ioque.acceptance.domain.exchange.Instrument;
import ru.ioque.acceptance.domain.exchange.InstrumentInList;
import ru.ioque.acceptance.domain.exchange.InstrumentStatistic;
import ru.ioque.acceptance.client.exchange.request.DisableUpdateInstrumentRequest;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ExchangeRestClient {
    RestTemplateFacade restTemplateFacade;
    ObjectMapper objectMapper;

    public void integrateWithDataSource() {
        restTemplateFacade.post("/instruments/integrate", null);
    }

    @SneakyThrows
    public void integrateTradingData() {
        restTemplateFacade.post("/instruments/daily-integrate", null);
    }

    public void clear() {
        restTemplateFacade.delete("/service/state");
    }

    public Exchange getExchange() {
        return restTemplateFacade.get("/exchange", Exchange.class);
    }

    @SneakyThrows
    public List<InstrumentInList> getInstruments() {
        return objectMapper.readValue(restTemplateFacade.get("/instruments", String.class), new TypeReference<>(){});
    }

    @SneakyThrows
    public void enableUpdateInstruments(EnableUpdateInstrumentRequest request) {
        restTemplateFacade.patch("/instruments/enable-update", objectMapper.writeValueAsString(request), String.class);
    }

    @SneakyThrows
    public void disableUpdateInstruments(DisableUpdateInstrumentRequest request) {
        restTemplateFacade.patch("/instruments/disable-update", objectMapper.writeValueAsString(request), String.class);
    }

    public Instrument getInstrumentBy(UUID id) {
        return restTemplateFacade.get("/instruments/" + id, Instrument.class);
    }

    public InstrumentStatistic getInstrumentStatisticBy(UUID id) {
        return restTemplateFacade.get("/instruments/" + id + "/statistic", InstrumentStatistic.class);
    }
}
