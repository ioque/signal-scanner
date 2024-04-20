package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.TradingSnapshotsRepository;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;
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
            .map(instrumentId -> TradingSnapshot.builder()
                .instrumentId(instrumentId)
                .ticker(fakeDatasourceRepository.getInstrumentBy(instrumentId).getTicker())
                .waPriceSeries(getHistoryBy(instrumentId)
                    .filter(row -> Objects.nonNull(row.getWaPrice()) && row.getWaPrice() > 0)
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getWaPrice(), dailyValue.getTradeDate()))
                    .toList()
                )
                .todayValueSeries(getIntradayBy(instrumentId)
                    .filter(row -> row.getDateTime().toLocalDate().equals(dateTimeProvider.nowDate()))
                    .map(intradayValue -> new TimeSeriesValue<>(
                        intradayValue.getValue(),
                        intradayValue.getDateTime().toLocalTime()
                    ))
                    .toList()
                )
                .closePriceSeries(getHistoryBy(instrumentId)
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getClosePrice(), dailyValue.getTradeDate()))
                    .toList())
                .openPriceSeries(getHistoryBy(instrumentId)
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getOpenPrice(), dailyValue.getTradeDate()))
                    .toList())
                .valueSeries(getHistoryBy(instrumentId)
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getValue(), dailyValue.getTradeDate()))
                    .toList())
                .todayPriceSeries(getIntradayBy(instrumentId)
                    .filter(row -> row.getDateTime().toLocalDate().equals(dateTimeProvider.nowDate()))
                    .map(intradayValue -> new TimeSeriesValue<>(
                        intradayValue.getPrice(),
                        intradayValue.getDateTime().toLocalTime()
                    ))
                    .toList())
                .build()).toList();
    }

    private Stream<IntradayValue> getIntradayBy(InstrumentId instrumentId) {
        return intradayValueRepository.getAllBy(instrumentId);
    }

    private Stream<HistoryValue> getHistoryBy(InstrumentId instrumentId) {
        return historyValueRepository.getAllBy(instrumentId);
    }
}
