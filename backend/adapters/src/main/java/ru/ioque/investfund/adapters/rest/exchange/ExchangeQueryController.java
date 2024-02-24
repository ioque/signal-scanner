package ru.ioque.investfund.adapters.rest.exchange;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.exchange.response.ExchangeResponse;
import ru.ioque.investfund.adapters.rest.exchange.response.InstrumentInListResponse;
import ru.ioque.investfund.adapters.rest.exchange.response.InstrumentResponse;
import ru.ioque.investfund.adapters.rest.exchange.response.InstrumentStatisticResponse;
import ru.ioque.investfund.adapters.storage.jpa.JpaInstrumentQueryRepository;
import ru.ioque.investfund.adapters.storage.jpa.filter.InstrumentFilterParams;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ExchangeRepository;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@Tag(name = "ExchangeQueryController", description = "Контроллер запросов к модулю \"EXCHANGE\"")
public class ExchangeQueryController {
    DateTimeProvider dateTimeProvider;
    JpaInstrumentQueryRepository instrumentQueryRepository;
    ExchangeRepository exchangeRepository;

    @GetMapping("/api/v1/exchange")
    public ExchangeResponse getExchange() {
        return ExchangeResponse.fromDomain(exchangeRepository.get());
    }

    @GetMapping("/api/v1/instruments/{id}")
    public InstrumentResponse getInstrument(@PathVariable UUID id) {
        return InstrumentResponse
            .fromDomain(
                instrumentQueryRepository
                    .getWithTradingDataBy(
                        id,
                        dateTimeProvider.nowDate()
                    )
            );
    }

    @GetMapping("/api/v1/instruments")
    public List<InstrumentInListResponse> getInstruments(
        @RequestParam String ticker,
        @RequestParam String type,
        @RequestParam String shortname,
        @RequestParam(defaultValue = "0") Integer pageNumber,
        @RequestParam(defaultValue = "100") Integer pageSize,
        @RequestParam(defaultValue = "ASC") String orderValue,
        @RequestParam(defaultValue = "shortName") String orderField
    ) {
        return instrumentQueryRepository
            .getAll(
                new InstrumentFilterParams(ticker, type, shortname, pageNumber, pageSize, orderValue, orderField)
            )
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
