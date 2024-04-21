package ru.ioque.investfund.adapters.datasource;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.datasource.client.DatasourceRestClient;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.datasource.dto.HistoryBatch;
import ru.ioque.investfund.application.datasource.dto.IntradayBatch;
import ru.ioque.investfund.application.datasource.dto.instrument.InstrumentDto;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HttpDatasourceProvider implements DatasourceProvider {
    DatasourceRestClient moexClient;
    DateTimeProvider dateTimeProvider;

    @Override
    @SneakyThrows
    public List<InstrumentDetails> fetchInstrumentDetails(Datasource datasource) {
        return moexClient
            .fetchInstruments(datasource.getUrl())
            .stream()
            .map(InstrumentDto::toDetails)
            .toList();
    }

    @Override
    @SneakyThrows
    public HistoryBatch fetchAggregateHistory(Datasource datasource, Instrument instrument) {
        return new HistoryBatch(
            moexClient
                .fetchHistory(
                    datasource.getUrl(),
                    instrument.getTicker().getValue(),
                    instrument.historyLeftBound(dateTimeProvider.nowDate()),
                    instrument.historyRightBound(dateTimeProvider.nowDate())
                )
        );
    }

    @Override
    @SneakyThrows
    public IntradayBatch fetchIntradayValues(Datasource datasource, Instrument instrument) {
        return new IntradayBatch(
            moexClient
                .fetchIntradayValues(
                    datasource.getUrl(),
                    instrument.getTicker().getValue(),
                    instrument.getLastTradingNumber()
                )
        );
    }
}
