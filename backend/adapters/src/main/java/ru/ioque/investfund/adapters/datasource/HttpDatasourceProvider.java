package ru.ioque.investfund.adapters.datasource;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.datasource.client.DatasourceRestClient;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.HistoryBatch;
import ru.ioque.investfund.domain.datasource.value.InstrumentBatch;
import ru.ioque.investfund.domain.datasource.value.IntradayBatch;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HttpDatasourceProvider implements DatasourceProvider {
    DatasourceRestClient moexClient;
    UUIDProvider uuidProvider;
    DateTimeProvider dateTimeProvider;

    @Override
    @SneakyThrows
    public InstrumentBatch fetchInstruments(Datasource datasource) {
        return new InstrumentBatch(
            moexClient
                .fetchInstruments(datasource.getUrl())
                .stream()
                .map(dto -> dto.toDomain(uuidProvider.generate(), datasource.getId()))
                .toList()
        );
    }

    @Override
    @SneakyThrows
    public HistoryBatch fetchHistoryBy(Datasource datasource, Instrument instrument) {
        return new HistoryBatch(
            moexClient
                .fetchHistory(
                    datasource.getUrl(),
                    instrument.getTicker(),
                    instrument.historyLeftBound(dateTimeProvider.nowDate()),
                    instrument.historyRightBound(dateTimeProvider.nowDate())
                )
                .stream()
                .map(row -> row.toDomain(datasource.getId()))
                .toList()
        );
    }

    @Override
    @SneakyThrows
    public IntradayBatch fetchIntradayValuesBy(Datasource datasource, Instrument instrument) {
        return new IntradayBatch(
            moexClient
                .fetchIntradayValues(
                    datasource.getUrl(),
                    instrument.getTicker(),
                    instrument.getLastTradingNumber()
                )
                .stream()
                .map(row -> row.toDomain(datasource.getId()))
                .filter(row -> row.getNumber() > instrument.getLastTradingNumber())
                .toList()
        );
    }
}
