package ru.ioque.investfund.adapters.persistence;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.HistoryValueEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.IntradayValueEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaHistoryValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaIntradayValueRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.TradingSnapshotsProvider;
import ru.ioque.investfund.domain.scanner.value.TimeSeriesValue;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PsqlTradingSnapshotsProvider implements TradingSnapshotsProvider {
    JpaHistoryValueRepository jpaHistoryValueRepository;
    JpaIntradayValueRepository jpaIntradayValueRepository;
    DateTimeProvider dateTimeProvider;

    @Override
    public List<TradingSnapshot> findBy(UUID datasourceId, List<String> tickers) {
        var histories = jpaHistoryValueRepository
            .findAllByDatasourceIdAndTickerIn(datasourceId, tickers)
            .stream()
            .collect(Collectors.groupingBy(HistoryValueEntity::getTicker));
        var intradayValues = jpaIntradayValueRepository
            .findAllBy(datasourceId, tickers, dateTimeProvider.nowDate().atStartOfDay())
            .stream()
            .collect(Collectors.groupingBy(IntradayValueEntity::getTicker));
        return tickers
            .stream()
            .map(ticker -> TradingSnapshot.builder()
                .ticker(ticker)
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
