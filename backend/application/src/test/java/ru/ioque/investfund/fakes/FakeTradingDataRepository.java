package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.TradingDataRepository;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;
import ru.ioque.investfund.domain.scanner.value.TimeSeriesValue;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class FakeTradingDataRepository implements TradingDataRepository {
    FakeDatasourceRepository datasourceRepository;
    DateTimeProvider dateTimeProvider;

    public FakeTradingDataRepository(FakeDatasourceRepository datasourceRepository,
                                     DateTimeProvider dateTimeProvider) {
        this.datasourceRepository = datasourceRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public List<TradingSnapshot> findBy(UUID datasourceId, List<String> tickers) {
        if (tickers == null || tickers.isEmpty()) return List.of();
        return tickers
            .stream()
            .map(ticker -> TradingSnapshot.builder()
                .ticker(ticker)
                .waPriceSeries(getHistoryBy(datasourceId, ticker)
                    .filter(row -> Objects.nonNull(row.getWaPrice()) && row.getWaPrice() > 0)
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getWaPrice(), dailyValue.getTradeDate()))
                    .toList()
                )
                .todayValueSeries(getIntradayBy(datasourceId, ticker)
                    .filter(row -> row.getDateTime().toLocalDate().equals(dateTimeProvider.nowDate()))
                    .map(intradayValue -> new TimeSeriesValue<>(
                        intradayValue.getValue(),
                        intradayValue.getDateTime().toLocalTime()
                    ))
                    .toList()
                )
                .closePriceSeries(getHistoryBy(datasourceId, ticker)
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getClosePrice(), dailyValue.getTradeDate()))
                    .toList())
                .openPriceSeries(getHistoryBy(datasourceId, ticker)
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getOpenPrice(), dailyValue.getTradeDate()))
                    .toList())
                .valueSeries(getHistoryBy(datasourceId, ticker)
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getValue(), dailyValue.getTradeDate()))
                    .toList())
                .todayPriceSeries(getIntradayBy(datasourceId, ticker)
                    .filter(row -> row.getDateTime().toLocalDate().equals(dateTimeProvider.nowDate()))
                    .map(intradayValue -> new TimeSeriesValue<>(
                        intradayValue.getPrice(),
                        intradayValue.getDateTime().toLocalTime()
                    ))
                    .toList())
                .build()).toList();
    }

    private Stream<IntradayValue> getIntradayBy(UUID datasourceId, String ticker) {
        return datasourceRepository.getIntradayBy(datasourceId, ticker);
    }

    private Stream<HistoryValue> getHistoryBy(UUID datasourceId, String ticker) {
        return datasourceRepository.getHistoryBy(datasourceId, ticker);
    }
}
