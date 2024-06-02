package ru.ioque.investfund.adapters.service.datasource;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.service.datasource.client.DatasourceRestClient;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.adapters.service.datasource.dto.history.AggregatedHistoryDto;
import ru.ioque.investfund.adapters.service.datasource.dto.instrument.InstrumentDto;
import ru.ioque.investfund.adapters.service.datasource.dto.intraday.IntradayDataDto;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetail;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HttpDatasourceProvider implements DatasourceProvider {
    DatasourceRestClient moexClient;

    @Override
    @SneakyThrows
    public List<InstrumentDetail> fetchInstruments(Datasource datasource) {
        return moexClient.fetchInstruments(datasource.getUrl()).stream().map(InstrumentDto::toDetails).toList();
    }

    @Override
    @SneakyThrows
    public List<AggregatedTotals> fetchAggregateHistory(Datasource datasource, Instrument instrument, LocalDate from, LocalDate to) {
        return moexClient
            .fetchHistory(datasource.getUrl(), instrument.getTicker().getValue(), from, to)
            .stream()
            .map(AggregatedHistoryDto::toAggregateHistory)
            .toList();
    }

    @Override
    @SneakyThrows
    public List<IntradayData> fetchIntradayValues(Datasource datasource, Instrument instrument, Long from) {
        return moexClient
            .fetchIntradayValues(datasource.getUrl(), instrument.getTicker().getValue(), from)
            .stream()
            .map(IntradayDataDto::toIntradayData)
            .toList();
    }
}
