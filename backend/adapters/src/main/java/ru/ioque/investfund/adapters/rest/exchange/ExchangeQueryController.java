package ru.ioque.investfund.adapters.rest.exchange;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.exchange.response.ExchangeResponse;
import ru.ioque.investfund.adapters.rest.exchange.response.InstrumentInListResponse;
import ru.ioque.investfund.adapters.rest.exchange.response.InstrumentResponse;
import ru.ioque.investfund.adapters.rest.exchange.response.InstrumentStatisticResponse;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.application.modules.exchange.ExchangeManager;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class ExchangeQueryController {
    ExchangeRepository exchangeRepository;
    ExchangeManager exchangeManager;

    @GetMapping("/api/v1/exchange")
    public ExchangeResponse getExchange() {
        return ExchangeResponse.fromDomain(exchangeRepository.get());
    }

    @GetMapping("/api/v1/exchange/instruments/{id}")
    public InstrumentResponse getInstrument(@PathVariable UUID id) {
        return InstrumentResponse.fromDomain(exchangeManager.getInstrumentBy(id));
    }

    @GetMapping("/api/v1/exchange/instruments")
    public List<InstrumentInListResponse> getInstruments() {
        return exchangeManager
            .getInstruments()
            .stream()
            .map(InstrumentInListResponse::fromDomain)
            .toList();
    }

    @GetMapping("/api/v1/exchange/instruments/{id}/statistic")
    public InstrumentStatisticResponse getInstrumentStatistic(@PathVariable UUID id) {
        return InstrumentStatisticResponse.fromDomain(exchangeManager.getInstrumentBy(id).calcStatistic());
    }
}
