package ru.ioque.investfund.adapters.rest.exchange;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.ResourceNotFoundException;
import ru.ioque.investfund.adapters.rest.exchange.response.ExchangeResponse;
import ru.ioque.investfund.adapters.rest.exchange.response.InstrumentInListResponse;
import ru.ioque.investfund.adapters.rest.exchange.response.InstrumentResponse;
import ru.ioque.investfund.adapters.storage.jpa.InstrumentQueryRepository;
import ru.ioque.investfund.adapters.storage.jpa.filter.InstrumentFilterParams;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ExchangeEntityRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@Tag(name = "ExchangeQueryController", description = "Контроллер запросов к модулю \"EXCHANGE\"")
public class ExchangeQueryController {
    DateTimeProvider dateTimeProvider;
    ExchangeEntityRepository exchangeRepository;
    InstrumentQueryRepository instrumentQueryRepository;

    @GetMapping("/api/exchange")
    public ExchangeResponse getExchange() {
        return ExchangeResponse.fromEntity(
            exchangeRepository
                .findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Биржа не зарегистрирована."))
        );
    }

    @GetMapping("/api/instruments/{id}")
    public InstrumentResponse getInstrument(@PathVariable UUID id) {
        return InstrumentResponse
            .fromDomain(
                instrumentQueryRepository
                    .getWithTradingDataBy(
                        id,
                        dateTimeProvider.nowDate()
                    )
                    .orElseThrow(() -> new ResourceNotFoundException("Данные о финансовом инструменте не найдены."))
            );
    }

    @GetMapping("/api/instruments")
    public List<InstrumentInListResponse> getInstruments(
        @RequestParam(required = false) String ticker,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String shortname,
        @RequestParam(defaultValue = "0") Integer pageNumber,
        @RequestParam(defaultValue = "100") Integer pageSize,
        @RequestParam(defaultValue = "ASC") String orderValue,
        @RequestParam(defaultValue = "shortName") String orderField
    ) {
        return instrumentQueryRepository
            .getAll(new InstrumentFilterParams(ticker, type, shortname, pageNumber, pageSize, orderValue, orderField))
            .stream()
            .map(InstrumentInListResponse::fromDomain)
            .toList();
    }
}
