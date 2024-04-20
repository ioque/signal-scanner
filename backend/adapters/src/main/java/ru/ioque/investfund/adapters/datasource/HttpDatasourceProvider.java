package ru.ioque.investfund.adapters.datasource;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.datasource.client.DatasourceRestClient;
import ru.ioque.investfund.adapters.datasource.client.dto.history.HistoryValueDto;
import ru.ioque.investfund.adapters.datasource.client.dto.instrument.InstrumentDto;
import ru.ioque.investfund.adapters.datasource.client.dto.intraday.IntradayValueDto;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;
import ru.ioque.investfund.domain.datasource.value.history.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayValue;

import java.util.List;
import java.util.TreeSet;

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
    public TreeSet<HistoryValue> fetchHistoryBy(Datasource datasource, Instrument instrument) {
        return new TreeSet<>(
            moexClient
                .fetchHistory(
                    datasource.getUrl(),
                    instrument.getTicker().getValue(),
                    instrument.historyLeftBound(dateTimeProvider.nowDate()),
                    instrument.historyRightBound(dateTimeProvider.nowDate())
                )
                .stream()
                .map(HistoryValueDto::toHistoryValue)
                .toList()
        );
    }

    @Override
    @SneakyThrows
    public TreeSet<IntradayValue> fetchIntradayValuesBy(Datasource datasource, Instrument instrument) {
        return new TreeSet<>(
            moexClient
                .fetchIntradayValues(
                    datasource.getUrl(),
                    instrument.getTicker().getValue(),
                    instrument.getLastTradingNumber()
                )
                .stream()
                .map(IntradayValueDto::toIntradayValue)
                .filter(row -> row.getNumber() > instrument.getLastTradingNumber())
                .toList()
        );
    }
}
