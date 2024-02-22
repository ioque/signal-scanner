package ru.ioque.investfund.adapters.rest.exchange;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.exchange.response.ExchangeResponse;
import ru.ioque.investfund.adapters.rest.exchange.response.InstrumentInListResponse;
import ru.ioque.investfund.adapters.rest.exchange.response.InstrumentResponse;
import ru.ioque.investfund.adapters.rest.exchange.response.InstrumentStatisticResponse;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.application.adapters.InstrumentQueryRepository;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@Tag(name="ExchangeQueryController", description="Контроллер запросов к модулю \"EXCHANGE\"")
public class ExchangeQueryController {
    DateTimeProvider dateTimeProvider;
    InstrumentQueryRepository instrumentQueryRepository;
    ExchangeRepository exchangeRepository;

    @GetMapping("/api/v1/exchange")
    public ExchangeResponse getExchange() {
        return ExchangeResponse.fromDomain(exchangeRepository.get());
    }

    @GetMapping("/api/v1/instruments/{id}")
    public InstrumentResponse getInstrument(@PathVariable UUID id) {
        return InstrumentResponse.fromDomain(instrumentQueryRepository.getById(id));
    }

    @GetMapping("/api/v1/instruments")
    public List<InstrumentInListResponse> getInstruments() {
        return instrumentQueryRepository
            .getAll()
            .stream()
            .map(InstrumentInListResponse::fromDomain)
            .toList();
    }

    @GetMapping("/api/v1/instruments/{id}/statistic")
    public InstrumentStatisticResponse getInstrumentStatistic(@PathVariable UUID id) {
        return InstrumentStatisticResponse
            .fromDomain(
                instrumentQueryRepository
                    .getWithTradingDataBy(id, dateTimeProvider.nowDate())
                    .calcStatistic()
            );
    }
}
