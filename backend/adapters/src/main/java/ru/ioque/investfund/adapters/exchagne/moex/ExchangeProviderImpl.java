package ru.ioque.investfund.adapters.exchagne.moex;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.exchagne.moex.client.ExchangeRestClient;
import ru.ioque.investfund.adapters.exchagne.moex.client.dto.history.HistoryValueDto;
import ru.ioque.investfund.adapters.exchagne.moex.client.dto.intraday.IntradayValueDto;
import ru.ioque.investfund.application.adapters.ExchangeProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.HistoryValue;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ExchangeProviderImpl implements ExchangeProvider {
    ExchangeRestClient moexClient;
    UUIDProvider uuidProvider;

    @Override
    @SneakyThrows
    public List<Instrument> fetchInstruments() {
        return moexClient
            .fetchInstruments()
            .stream()
            .map(dto -> dto.toDomain(uuidProvider.generate()))
            .toList();
    }

    @Override
    @SneakyThrows
    public List<HistoryValue> fetchHistoryBy(
        String ticker,
        LocalDate from,
        LocalDate to
    ) {
        return moexClient
            .fetchHistory(
                ticker,
                from,
                to
            )
            .stream()
            .map(HistoryValueDto::toDomain)
            .toList();
    }

    @Override
    @SneakyThrows
    public List<IntradayValue> fetchIntradayValuesBy(String ticker, long lastNumber) {
        return moexClient
            .fetchIntradayValues(ticker, lastNumber)
            .stream()
            .map(IntradayValueDto::toDomain)
            .toList();
    }
}
