package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.DealResult;
import ru.ioque.investfund.domain.scanner.entity.FinInstrument;
import ru.ioque.investfund.domain.scanner.entity.SignalConfig;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.TimeSeriesValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FakeScannerRepository implements ScannerRepository {
    Map<UUID, SignalConfig> configMap = new HashMap<>();
    Map<UUID, SignalScanner> financialDataScannerMap = new HashMap<>();
    ExchangeRepository exchangeRepository;

    public FakeScannerRepository(ExchangeRepository exchangeRepository) {
        this.exchangeRepository = exchangeRepository;
    }

    @Override
    public void saveConfig(UUID scannerId, SignalConfig config) {
        configMap.put(scannerId, config);
        if (financialDataScannerMap.containsKey(scannerId)) {
            financialDataScannerMap.put(
                scannerId,
                config.factoryScanner(
                    scannerId,
                    financialDataScannerMap.get(scannerId).getLastExecutionDateTime().orElse(null),
                    getAllByInstrumentIdIn(config.getObjectIds()),
                    financialDataScannerMap.get(scannerId).getSignals()
                )
            );
        } else {
            financialDataScannerMap.put(
                scannerId,
                config.factoryScanner(
                    scannerId,
                    null,
                    getAllByInstrumentIdIn(config.getObjectIds()),
                    new ArrayList<>()
                )
            );
        }
    }

    @Override
    public Optional<SignalScanner> getBy(UUID id) {
        return Optional.ofNullable(financialDataScannerMap.get(id)).map(this::map);
    }

    private SignalScanner map(SignalScanner signalScanner) {
        return SignalScanner.builder()
            .id(signalScanner.getId())
            .workPeriodInMinutes(signalScanner.getWorkPeriodInMinutes())
            .algorithm(signalScanner.getAlgorithm())
            .description(signalScanner.getDescription())
            .signals(signalScanner.getSignals())
            .lastExecutionDateTime(signalScanner.getLastExecutionDateTime().orElse(null))
            .finInstruments(getAllByInstrumentIdIn(signalScanner.getObjectIds()))
            .build();
    }

    @Override
    public void save(SignalScanner dataScanner) {
        this.financialDataScannerMap.put(dataScanner.getId(), dataScanner);
    }

    @Override
    public List<SignalScanner> getAll() {
        return financialDataScannerMap.values().stream().map(this::map).toList();
    }

    private List<FinInstrument> getAllByInstrumentIdIn(List<UUID> instrumentIds) {
        return instrumentIds.stream().map(id -> {
            Instrument instrument = exchangeRepository.get().getInstruments().stream().filter(row -> row
                .getId()
                .equals(id)).findFirst().orElseThrow();
            return FinInstrument.builder()
                .instrumentId(instrument.getId())
                .ticker(instrument.getTicker())
                .waPriceSeries(instrument
                    .getDailyValues()
                    .stream()
                    .filter(row -> row.getClass().equals(DealResult.class))
                    .map(DealResult.class::cast)
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getWaPrice(), dailyValue.getTradeDate()))
                    .toList()
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
                .closePriceSeries(instrument
                    .getDailyValues()
                    .stream()
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getClosePrice(), dailyValue.getTradeDate()))
                    .toList())
                .openPriceSeries(instrument
                    .getDailyValues()
                    .stream()
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getOpenPrice(), dailyValue.getTradeDate()))
                    .toList())
                .valueSeries(instrument
                    .getDailyValues()
                    .stream()
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getValue(), dailyValue.getTradeDate()))
                    .toList())
                .todayPriceSeries(instrument
                    .getIntradayValues()
                    .stream()
                    .map(intradayValue -> new TimeSeriesValue<>(
                        intradayValue.getPrice(),
                        intradayValue.getDateTime().toLocalTime()
                    ))
                    .toList())
                .build();
        }).toList();
    }
}
