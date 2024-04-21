package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.TradingSnapshotsRepository;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.value.TimeSeriesValue;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.util.List;
import java.util.Objects;

public class FakeTradingSnapshotsRepository implements TradingSnapshotsRepository {
    FakeDatasourceRepository fakeDatasourceRepository;
    DateTimeProvider dateTimeProvider;

    public FakeTradingSnapshotsRepository(
        FakeDatasourceRepository fakeDatasourceRepository,
        DateTimeProvider dateTimeProvider
    ) {
        this.fakeDatasourceRepository = fakeDatasourceRepository;
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
                    .ticker(ticker)
                    .lastPrice(instrument.getTradingState() != null ? instrument.getTradingState().getLastPrice() : null)
                    .firstPrice(instrument.getTradingState() != null ? instrument.getTradingState().getOpenPrice() : null)
                    .value(instrument.getTradingState() != null ? instrument.getTradingState().getValue() : null)
                    .waPriceSeries(instrument.getAggregateHistories().stream()
                        .filter(row -> Objects.nonNull(row.getWaPrice()) && row.getWaPrice() > 0)
                        .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getWaPrice(), dailyValue.getDate()))
                        .toList()
                    )
                    .closePriceSeries(instrument.getAggregateHistories().stream()
                        .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getClosePrice(), dailyValue.getDate()))
                        .toList())
                    .openPriceSeries(instrument.getAggregateHistories().stream()
                        .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getOpenPrice(), dailyValue.getDate()))
                        .toList())
                    .valueSeries(instrument.getAggregateHistories().stream()
                        .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getValue(), dailyValue.getDate()))
                        .toList())
                    .build();
            }).toList();
    }
}
