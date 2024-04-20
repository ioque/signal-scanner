package ru.ioque.investfund.adapters.datasource;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.datasource.client.DatasourceRestClient;
import ru.ioque.investfund.adapters.datasource.client.dto.instrument.InstrumentDto;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.history.HistoryBatch;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayBatch;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HttpDatasourceProvider implements DatasourceProvider {
    DatasourceRestClient moexClient;
    UUIDProvider uuidProvider;
    DateTimeProvider dateTimeProvider;

    @Override
    @SneakyThrows
    public List<Instrument> fetchInstrumentDetails(Datasource datasource) {
        return moexClient
            .fetchInstruments(datasource.getUrl())
            .stream()
            .map(InstrumentDto::toDomain)
            .toList();
    }

    @Override
    @SneakyThrows
    public HistoryBatch fetchHistoryBy(Datasource datasource, Instrument instrument) {
        return new HistoryBatch(
            moexClient
                .fetchHistory(
                    datasource.getUrl(),
                    instrument.getId().getTicker().getValue(),
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
                    instrument.getId().getTicker().getValue(),
                    instrument.getLastTradingNumber()
                )
                .stream()
                .map(row -> row.toDomain(datasource.getId()))
                .filter(row -> row.getNumber() > instrument.getLastTradingNumber())
                .toList()
        );
    }
}
