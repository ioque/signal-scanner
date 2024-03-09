package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.DealResult;
import ru.ioque.investfund.domain.scanner.entity.FinInstrument;
import ru.ioque.investfund.domain.scanner.entity.SignalScannerBot;
import ru.ioque.investfund.domain.scanner.value.TimeSeriesValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FakeScannerRepository implements ScannerRepository {
    Map<UUID, SignalScannerBot> financialDataScannerMap = new HashMap<>();
    ExchangeRepository exchangeRepository;

    public FakeScannerRepository(ExchangeRepository exchangeRepository) {
        this.exchangeRepository = exchangeRepository;
    }

    @Override
    public Optional<SignalScannerBot> getBy(UUID id) {
        return Optional.ofNullable(financialDataScannerMap.get(id)).map(this::map);
    }

    private SignalScannerBot map(SignalScannerBot signalScannerBot) {
        return SignalScannerBot.builder()
            .id(signalScannerBot.getId())
            .config(signalScannerBot.getConfig())
            .description(signalScannerBot.getDescription())
            .signals(signalScannerBot.getSignals())
            .lastExecutionDateTime(signalScannerBot.getLastExecutionDateTime().orElse(null))
            .finInstruments(getAllByInstrumentIdIn(signalScannerBot.getObjectIds()))
            .build();
    }

    @Override
    public void save(SignalScannerBot dataScanner) {
        this.financialDataScannerMap.put(dataScanner.getId(), dataScanner);
    }

    @Override
    public List<SignalScannerBot> getAll() {
        return financialDataScannerMap.values().stream().map(this::map).toList();
    }

    private List<FinInstrument> getAllByInstrumentIdIn(List<UUID> instrumentIds) {
        return instrumentIds.stream().map(id -> {
            Instrument instrument = exchangeRepository.get().getInstruments().stream().filter(row -> row.getId().equals(id)).findFirst().orElseThrow();
            return FinInstrument.builder()
                .instrumentId(instrument.getId())
                .ticker(instrument.getTicker())
                .buyToSellValueRatio(instrument.getBuyToSellValueRatio())
                .waPriceSeries(instrument.getDailyValues()
                    .stream()
                    .filter(row -> row.getClass().equals(DealResult.class))
                    .map(DealResult.class::cast)
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getWaPrice(), dailyValue.getTradeDate())).toList()
                )
                .todayValueSeries(
                    instrument
                        .getIntradayValues()
                        .stream()
                        .map(intradayValue -> new TimeSeriesValue<>(
                            intradayValue.getValue(),
                            intradayValue.getDateTime().toLocalTime()
                        ))
                        .toList()
                )
                .closePriceSeries(instrument.getDailyValues().stream().map(dailyValue -> new TimeSeriesValue<>(dailyValue.getClosePrice(), dailyValue.getTradeDate())).toList())
                .openPriceSeries(instrument.getDailyValues().stream().map(dailyValue -> new TimeSeriesValue<>(dailyValue.getOpenPrice(), dailyValue.getTradeDate())).toList())
                .valueSeries(instrument.getDailyValues().stream().map(dailyValue -> new TimeSeriesValue<>(dailyValue.getValue(), dailyValue.getTradeDate())).toList())
                .todayPriceSeries(instrument.getIntradayValues().stream().map(intradayValue -> new TimeSeriesValue<>(intradayValue.getPrice(), intradayValue.getDateTime().toLocalTime())).toList())
                .build();
        }).toList();
    }
}
