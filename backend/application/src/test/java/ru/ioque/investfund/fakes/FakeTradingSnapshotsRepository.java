package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.TradingSnapshotsRepository;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.history.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayValue;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.value.TimeSeriesValue;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class FakeTradingSnapshotsRepository implements TradingSnapshotsRepository {
    FakeDatasourceRepository fakeDatasourceRepository;
    FakeIntradayValueRepository intradayValueRepository;
    FakeHistoryValueRepository historyValueRepository;
    DateTimeProvider dateTimeProvider;

    public FakeTradingSnapshotsRepository(
        FakeDatasourceRepository fakeDatasourceRepository,
        FakeIntradayValueRepository intradayValueRepository,
        FakeHistoryValueRepository historyValueRepository,
        DateTimeProvider dateTimeProvider
    ) {
        this.fakeDatasourceRepository = fakeDatasourceRepository;
        this.intradayValueRepository = intradayValueRepository;
        this.historyValueRepository = historyValueRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public List<TradingSnapshot> findAllBy(List<InstrumentId> instrumentIds) {
        if (instrumentIds == null || instrumentIds.isEmpty()) return List.of();
        return instrumentIds
            .stream()
            .map(instrumentId -> {
                Instrument instrument = fakeDatasourceRepository.getInstrumentBy(instrumentId);
                Ticker ticker = instrument.getTicker();
                return TradingSnapshot.builder()
                    .instrumentId(instrumentId)
                    .ticker(ticker)
                    .waPriceSeries(getHistoryBy(ticker)
                        .filter(row -> Objects.nonNull(row.getWaPrice()) && row.getWaPrice() > 0)
                        .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getWaPrice(), dailyValue.getTradeDate()))
                        .toList()
                    )
                    .todayValueSeries(getIntradayBy(ticker)
                        .filter(row -> row.getDateTime().toLocalDate().equals(dateTimeProvider.nowDate()))
                        .map(intradayValue -> new TimeSeriesValue<>(
                            intradayValue.getValue(),
                            intradayValue.getDateTime().toLocalTime()
                        ))
                        .toList()
                    )
                    .closePriceSeries(getHistoryBy(ticker)
                        .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getClosePrice(), dailyValue.getTradeDate()))
                        .toList())
                    .openPriceSeries(getHistoryBy(ticker)
                        .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getOpenPrice(), dailyValue.getTradeDate()))
                        .toList())
                    .valueSeries(getHistoryBy(ticker)
                        .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getValue(), dailyValue.getTradeDate()))
                        .toList())
                    .todayPriceSeries(getIntradayBy(ticker)
                        .filter(row -> row.getDateTime().toLocalDate().equals(dateTimeProvider.nowDate()))
                        .map(intradayValue -> new TimeSeriesValue<>(
                            intradayValue.getPrice(),
                            intradayValue.getDateTime().toLocalTime()
                        ))
                        .toList())
                    .build();
            }).toList();
    }

    private Stream<IntradayValue> getIntradayBy(Ticker ticker) {
        return intradayValueRepository.getAllBy(ticker);
    }

    private Stream<HistoryValue> getHistoryBy(Ticker ticker) {
        return historyValueRepository.getAllBy(ticker);
    }
}
