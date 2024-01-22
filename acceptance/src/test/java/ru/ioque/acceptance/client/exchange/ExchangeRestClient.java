package ru.ioque.acceptance.client.exchange;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.ioque.acceptance.client.RestTemplateFacade;
import ru.ioque.acceptance.domain.exchange.Exchange;
import ru.ioque.acceptance.domain.exchange.Instrument;
import ru.ioque.acceptance.domain.exchange.InstrumentInList;
import ru.ioque.acceptance.domain.exchange.InstrumentStatistic;

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
        restTemplateFacade.delete("/instruments");
    }

    public Exchange getExchange() {
        return restTemplateFacade.get("/exchange", Exchange.class);
    }

    @SneakyThrows
    public List<InstrumentInList> getInstruments() {
        return objectMapper.readValue(restTemplateFacade.get("/exchange/instruments", String.class), new TypeReference<>(){});
    }

    public void enableUpdateInstruments(List<UUID> list) {

    }

    public void tradingDataIntegrate() {
    }

    public Instrument getInstrumentBy(String afks) {
        return null;
    }

    public InstrumentStatistic getInstrumentStatisticBy(String afks) {
        return null;
    }
}
