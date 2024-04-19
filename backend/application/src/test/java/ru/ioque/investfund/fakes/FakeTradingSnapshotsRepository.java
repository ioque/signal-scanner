package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.TradingSnapshotsRepository;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;
import ru.ioque.investfund.domain.datasource.value.Ticker;
import ru.ioque.investfund.domain.scanner.value.TimeSeriesValue;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class FakeTradingSnapshotsRepository implements TradingSnapshotsRepository {
    FakeIntradayValueRepository intradayValueRepository;
    FakeHistoryValueRepository historyValueRepository;
    DateTimeProvider dateTimeProvider;

    public FakeTradingSnapshotsRepository(
        FakeIntradayValueRepository intradayValueRepository,
        FakeHistoryValueRepository historyValueRepository,
        DateTimeProvider dateTimeProvider
    ) {
        this.intradayValueRepository = intradayValueRepository;
        this.historyValueRepository = historyValueRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public List<TradingSnapshot> findAllBy(DatasourceId datasourceId, List<String> tickers) {
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
                .todayValueSeries(getIntradayBy(datasourceId, new InstrumentId(new Ticker(ticker)))
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
                .todayPriceSeries(getIntradayBy(datasourceId, new InstrumentId(new Ticker(ticker)))
                    .filter(row -> row.getDateTime().toLocalDate().equals(dateTimeProvider.nowDate()))
                    .map(intradayValue -> new TimeSeriesValue<>(
                        intradayValue.getPrice(),
                        intradayValue.getDateTime().toLocalTime()
                    ))
                    .toList())
                .build()).toList();
    }

    private Stream<IntradayValue> getIntradayBy(DatasourceId datasourceId, InstrumentId instrumentId) {
        return intradayValueRepository.getAllBy(datasourceId, instrumentId);
    }

    private Stream<HistoryValue> getHistoryBy(DatasourceId datasourceId, String ticker) {
        return historyValueRepository.getAllBy(datasourceId, ticker);
    }
}
