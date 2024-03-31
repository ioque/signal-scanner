package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.application.adapters.FinInstrumentRepository;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.scanner.entity.FinInstrument;
import ru.ioque.investfund.domain.scanner.value.TimeSeriesValue;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FakeFinInstrumentRepository implements FinInstrumentRepository {
    ExchangeRepository exchangeRepository;
    DateTimeProvider dateTimeProvider;

    public FakeFinInstrumentRepository(ExchangeRepository exchangeRepository, DateTimeProvider dateTimeProvider) {
        this.exchangeRepository = exchangeRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public List<FinInstrument> getByIdIn(List<UUID> instrumentIds) {
        if (instrumentIds == null || instrumentIds.isEmpty()) return List.of();
        return instrumentIds.stream().map(id -> {
            Instrument instrument = exchangeRepository
                .get()
                .orElseThrow()
                .getInstruments()
                .stream()
                .filter(row -> row.getId().equals(id))
                .findFirst()
                .orElseThrow();
            return FinInstrument.builder()
                .instrumentId(instrument.getId())
                .ticker(instrument.getTicker())
                .waPriceSeries(instrument
                    .getHistoryValues()
                    .stream()
                    .filter(row -> Objects.nonNull(row.getWaPrice()) && row.getWaPrice() > 0)
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getWaPrice(), dailyValue.getTradeDate()))
                    .toList()
                )
                .todayValueSeries(
                    instrument
                        .getIntradayValues()
                        .stream()
                        .filter(row -> row.getDateTime().toLocalDate().equals(dateTimeProvider.nowDate()))
                        .map(intradayValue -> new TimeSeriesValue<>(
                            intradayValue.getValue(),
                            intradayValue.getDateTime().toLocalTime()
                        ))
                        .toList()
                )
                .closePriceSeries(instrument
                    .getHistoryValues()
                    .stream()
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getClosePrice(), dailyValue.getTradeDate()))
                    .toList())
                .openPriceSeries(instrument
                    .getHistoryValues()
                    .stream()
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getOpenPrice(), dailyValue.getTradeDate()))
                    .toList())
                .valueSeries(instrument
                    .getHistoryValues()
                    .stream()
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getValue(), dailyValue.getTradeDate()))
                    .toList())
                .todayPriceSeries(instrument
                    .getIntradayValues()
                    .stream()
                    .filter(row -> row.getDateTime().toLocalDate().equals(dateTimeProvider.nowDate()))
                    .map(intradayValue -> new TimeSeriesValue<>(
                        intradayValue.getPrice(),
                        intradayValue.getDateTime().toLocalTime()
                    ))
                    .toList())
                .build();
        }).toList();
    }
}
