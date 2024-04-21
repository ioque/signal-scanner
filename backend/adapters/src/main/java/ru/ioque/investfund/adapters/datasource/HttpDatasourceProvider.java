package ru.ioque.investfund.adapters.datasource;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.datasource.client.DatasourceRestClient;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.datasource.integration.dto.history.AggregatedHistoryDto;
import ru.ioque.investfund.application.datasource.integration.dto.instrument.InstrumentDto;
import ru.ioque.investfund.application.datasource.integration.dto.intraday.IntradayDataDto;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HttpDatasourceProvider implements DatasourceProvider {
    DatasourceRestClient moexClient;
    DateTimeProvider dateTimeProvider;

    @Override
    @SneakyThrows
    public List<InstrumentDto> fetchInstruments(Datasource datasource) {
        return moexClient.fetchInstruments(datasource.getUrl());
    }

    @Override
    @SneakyThrows
    public List<AggregatedHistoryDto> fetchAggregateHistory(Datasource datasource, Instrument instrument) {
        return moexClient
            .fetchHistory(
                datasource.getUrl(),
                instrument.getTicker().getValue(),
                instrument.historyLeftBound(dateTimeProvider.nowDate()),
                instrument.historyRightBound(dateTimeProvider.nowDate())
            );
    }

    @Override
    @SneakyThrows
    public List<IntradayDataDto> fetchIntradayValues(Datasource datasource, Instrument instrument) {
        return moexClient
            .fetchIntradayValues(
                datasource.getUrl(),
                instrument.getTicker().getValue(),
                instrument.getLastTradingNumber()
            );
    }
}
