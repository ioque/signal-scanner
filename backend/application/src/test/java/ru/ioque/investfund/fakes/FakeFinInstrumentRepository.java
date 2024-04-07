package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.FinInstrumentRepository;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.scanner.entity.FinInstrument;
import ru.ioque.investfund.domain.scanner.value.TimeSeriesValue;

import java.util.List;
import java.util.Objects;

public class FakeFinInstrumentRepository implements FinInstrumentRepository {
    DatasourceRepository datasourceRepository;
    DateTimeProvider dateTimeProvider;

    public FakeFinInstrumentRepository(DatasourceRepository datasourceRepository, DateTimeProvider dateTimeProvider) {
        this.datasourceRepository = datasourceRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public List<FinInstrument> getBy(List<String> tickers) {
        if (tickers == null || tickers.isEmpty()) return List.of();
        return tickers.stream().map(ticker -> {
            Instrument instrument = datasourceRepository
                .getBy(dateTimeProvider.nowDate())
                .orElseThrow()
                .getInstruments()
                .stream()
                .filter(row -> row.getTicker().equals(ticker))
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
