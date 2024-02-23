package ru.ioque.acceptance.adapters.client.exchange;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.ioque.acceptance.adapters.client.RestTemplateFacade;
import ru.ioque.acceptance.adapters.client.exchange.request.EnableUpdateInstrumentRequest;
import ru.ioque.acceptance.domain.exchange.Exchange;
import ru.ioque.acceptance.domain.exchange.Instrument;
import ru.ioque.acceptance.domain.exchange.InstrumentInList;
import ru.ioque.acceptance.domain.exchange.InstrumentStatistic;
import ru.ioque.acceptance.adapters.client.exchange.request.DisableUpdateInstrumentRequest;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ExchangeRestClient {
    RestTemplateFacade restTemplateFacade;
    ObjectMapper objectMapper;

    public void synchronizeWithDataSource() {
        restTemplateFacade.post("/instruments/integrate", null);
    }

    @SneakyThrows
    public void integrateTradingData() {
        restTemplateFacade.post("/instruments/daily-integrate", null);
    }

    public Exchange getExchange() {
        return restTemplateFacade.get("/exchange", Exchange.class);
    }

    @SneakyThrows
    public List<InstrumentInList> getInstruments(String params) {
        String path = "/instruments" + (params == null || params.isEmpty() ? "" : ("?" + params));
        System.out.println(path);
        return objectMapper.readValue(restTemplateFacade.get(path, String.class), new TypeReference<>(){});
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
