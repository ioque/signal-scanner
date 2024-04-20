package ru.ioque.investfund.adapters.persistence;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.HistoryValueEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.IntradayValueEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaHistoryValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaIntradayValueRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.TradingSnapshotsRepository;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.value.TimeSeriesValue;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PsqlTradingSnapshotsRepository implements TradingSnapshotsRepository {
    JpaHistoryValueRepository jpaHistoryValueRepository;
    JpaIntradayValueRepository jpaIntradayValueRepository;
    DateTimeProvider dateTimeProvider;

    @Override
    public List<TradingSnapshot> findAllBy(DatasourceId datasourceId, List<InstrumentId> instrumentIds) {
        final List<String> tickers = instrumentIds.stream().map(id -> id.getTicker().getValue()).toList();
        final Map<String, List<HistoryValueEntity>> histories = jpaHistoryValueRepository
            .findAllByDatasourceIdAndTickerIn(datasourceId.getUuid(), tickers)
            .stream()
            .collect(Collectors.groupingBy(HistoryValueEntity::getTicker));
        final Map<String, List<IntradayValueEntity>> intradayValues = jpaIntradayValueRepository
            .findAllBy(datasourceId.getUuid(), tickers, dateTimeProvider.nowDate().atStartOfDay())
            .stream()
            .collect(Collectors.groupingBy(IntradayValueEntity::getTicker));
        return tickers
            .stream()
            .map(ticker -> TradingSnapshot.builder()
                .instrumentId(InstrumentId.from(Ticker.from(ticker)))
                .waPriceSeries(histories.getOrDefault(ticker, new ArrayList<>())
                    .stream()
                    .filter(row -> Objects.nonNull(row.getWaPrice()) && row.getWaPrice() > 0)
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getWaPrice(), dailyValue.getTradeDate()))
                    .toList()
                )
                .closePriceSeries(histories.getOrDefault(ticker, new ArrayList<>())
                    .stream()
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getClosePrice(), dailyValue.getTradeDate()))
                    .toList())
                .openPriceSeries(histories.getOrDefault(ticker, new ArrayList<>())
                    .stream()
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getOpenPrice(), dailyValue.getTradeDate()))
                    .toList())
                .valueSeries(histories.getOrDefault(ticker, new ArrayList<>())
                    .stream()
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getValue(), dailyValue.getTradeDate()))
                    .toList())
                .todayValueSeries(intradayValues.getOrDefault(ticker, new ArrayList<>())
                    .stream()
                    .map(intradayValue -> new TimeSeriesValue<>(
                        intradayValue.getValue(),
                        intradayValue.getDateTime().toLocalTime()
                    ))
                    .toList()
                )
                .todayPriceSeries(intradayValues.getOrDefault(ticker, new ArrayList<>())
                    .stream()
                    .map(intradayValue -> new TimeSeriesValue<>(
                        intradayValue.getPrice(),
                        intradayValue.getDateTime().toLocalTime()
                    ))
                    .toList())
                .build())
            .toList();
    }
}
