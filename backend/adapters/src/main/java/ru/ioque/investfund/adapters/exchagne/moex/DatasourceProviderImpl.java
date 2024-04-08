package ru.ioque.investfund.adapters.exchagne.moex;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.exchagne.moex.client.ExchangeRestClient;
import ru.ioque.investfund.adapters.exchagne.moex.client.dto.history.HistoryValueDto;
import ru.ioque.investfund.adapters.exchagne.moex.client.dto.intraday.IntradayValueDto;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DatasourceProviderImpl implements DatasourceProvider {
    ExchangeRestClient moexClient;
    UUIDProvider uuidProvider;
    DateTimeProvider dateTimeProvider;

    @Override
    @SneakyThrows
    public List<Instrument> fetchInstruments(Datasource datasource) {
        return moexClient
            .fetchInstruments(datasource.getUrl())
            .stream()
            .map(dto -> dto.toDomain(uuidProvider.generate()))
            .distinct()
            .toList();
    }

    @Override
    @SneakyThrows
    public List<HistoryValue> fetchHistoryBy(
        Datasource datasource,
        Instrument instrument
    ) {
        return moexClient
            .fetchHistory(
                datasource.getUrl(),
                instrument.getTicker(),
                instrument.historyLeftBound(dateTimeProvider.nowDate()),
                instrument.historyRightBound(dateTimeProvider.nowDate())
            )
            .stream()
            .map(HistoryValueDto::toDomain)
            .distinct()
            .toList();
    }

    @Override
    @SneakyThrows
    public List<IntradayValue> fetchIntradayValuesBy(
        Datasource datasource,
        Instrument instrument
    ) {
        return moexClient
            .fetchIntradayValues(
                datasource.getUrl(),
                instrument.getTicker(),
                instrument.getLastTradingNumber()
            )
            .stream()
            .map(IntradayValueDto::toDomain)
            .distinct()
            .toList();
    }
}
