package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ScannerConfigRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.domain.configurator.SignalScannerConfig;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.entity.TradingSnapshot;
import ru.ioque.investfund.domain.scanner.value.TimeSeriesValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class FakeScannerRepository implements ScannerRepository, ScannerConfigRepository {
    public Map<UUID, SignalScanner> scannerMap = new HashMap<>();
    FakeDatasourceRepository datasourceRepository;
    DateTimeProvider dateTimeProvider;

    public FakeScannerRepository(FakeDatasourceRepository datasourceRepository, DateTimeProvider dateTimeProvider) {
        this.datasourceRepository = datasourceRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public void save(SignalScanner dataScanner) {
        this.scannerMap.put(dataScanner.getId(), dataScanner);
    }

    @Override
    public List<SignalScanner> getAll() {
        scannerMap.values().stream().map(SignalScanner::getId).forEach(this::updateTradingSnapshots);
        return scannerMap.values().stream().toList();
    }

    @Override
    public void save(SignalScannerConfig config) {
        scannerMap.put(config.getId(), map(config));
    }

    @Override
    public boolean existsBy(UUID id) {
        return scannerMap.containsKey(id);
    }

    private Optional<SignalScanner> getBy(UUID id) {
        return Optional.ofNullable(scannerMap.get(id));
    }

    private void updateTradingSnapshots(UUID id) {
        if (scannerMap.containsKey(id)) {
            scannerMap.computeIfPresent(
                id,
                (k, scanner) -> SignalScanner.builder()
                    .id(scanner.getId())
                    .datasourceId(scanner.getDatasourceId())
                    .workPeriodInMinutes(scanner.getWorkPeriodInMinutes())
                    .algorithm(scanner.getAlgorithm())
                    .description(scanner.getDescription())
                    .signals(scanner.getSignals())
                    .lastExecutionDateTime(scanner.getLastExecutionDateTime().orElse(null))
                    .tradingSnapshots(createSnapshots(scanner.getDatasourceId(), scanner.getTickers()))
                    .build()
            );
        }
    }

    private SignalScanner map(SignalScannerConfig config) {
        return SignalScanner.builder()
            .id(config.getId())
            .datasourceId(config.getDatasourceId())
            .workPeriodInMinutes(config.getWorkPeriodInMinutes())
            .algorithm(config.getAlgorithmConfig().factoryAlgorithm())
            .description(config.getDescription())
            .signals(getBy(config.getId()).map(SignalScanner::getSignals).orElse(new ArrayList<>()))
            .lastExecutionDateTime(getBy(config.getId())
                .map(SignalScanner::getLastExecutionDateTime)
                .flatMap(r -> r)
                .orElse(null))
            .tradingSnapshots(createSnapshots(config.getDatasourceId(), config.getTickers()))
            .build();
    }

    private List<TradingSnapshot> createSnapshots(UUID datasourceId, List<String> tickers) {
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
