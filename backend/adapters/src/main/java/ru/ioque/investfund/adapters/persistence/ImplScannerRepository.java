package ru.ioque.investfund.adapters.persistence;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.HistoryValueEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.IntradayValueEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.AnomalyVolumeScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.CorrelationSectoralScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.PrefSimpleScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.ScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.SectoralRetardScannerEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaHistoryValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaIntradayValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaSignalScannerRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.domain.scanner.entity.AnomalyVolumeAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.CorrelationSectoralAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.PrefSimpleAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.SectoralRetardAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.entity.TradingSnapshot;
import ru.ioque.investfund.domain.scanner.value.TimeSeriesValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ImplScannerRepository implements ScannerRepository {
    JpaSignalScannerRepository signalScannerEntityRepository;
    JpaHistoryValueRepository jpaHistoryValueRepository;
    JpaIntradayValueRepository jpaIntradayValueRepository;
    DateTimeProvider dateTimeProvider;

    @Transactional(readOnly = true)
    public Optional<SignalScanner> getBy(UUID id) {
        return signalScannerEntityRepository
            .findById(id)
            .map(row -> row.toDomain(createSnapshots(row.getDatasourceId(), row.getTickers())));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SignalScanner> getAll() {
        return signalScannerEntityRepository
            .findAll()
            .stream()
            .map(row -> row.toDomain(createSnapshots(row.getDatasourceId(), row.getTickers())))
            .toList();
    }

    @Override
    @Transactional
    public void save(SignalScanner dataScanner) {
        signalScannerEntityRepository.save(toEntity(dataScanner));
    }

    private List<TradingSnapshot> createSnapshots(UUID datasourceId, List<String> tickers) {
        var histories = jpaHistoryValueRepository
            .findAllByDatasourceIdAndTickerIn(datasourceId, tickers)
            .stream()
            .collect(Collectors.groupingBy(HistoryValueEntity::getTicker));
        var intradayValues = jpaIntradayValueRepository
            .findAllBy(datasourceId, tickers, dateTimeProvider.nowDate().atStartOfDay())
            .stream()
            .collect(Collectors.groupingBy(IntradayValueEntity::getTicker));
        return tickers
            .stream()
            .map(ticker -> TradingSnapshot.builder()
                .ticker(ticker)
                .waPriceSeries(histories.getOrDefault(ticker, new ArrayList<>())
                    .stream()
                    .filter(row -> Objects.nonNull(row.getWaPrice()) && row.getWaPrice() > 0)
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getWaPrice(), dailyValue.getTradeDate()))
                    .toList()
                )
                .closePriceSeries(histories.getOrDefault(ticker, new ArrayList<>())
                    .stream()
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getClosePrice(), dailyValue.getTradeDate()))
                    .toList())
                .openPriceSeries(histories.getOrDefault(ticker, new ArrayList<>())
                    .stream()
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getOpenPrice(), dailyValue.getTradeDate()))
                    .toList())
                .valueSeries(histories.getOrDefault(ticker, new ArrayList<>())
                    .stream()
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getValue(), dailyValue.getTradeDate()))
                    .toList())
                .todayValueSeries(intradayValues.getOrDefault(ticker, new ArrayList<>())
                    .stream()
                    .map(intradayValue -> new TimeSeriesValue<>(
                        intradayValue.getValue(),
                        intradayValue.getDateTime().toLocalTime()
                    ))
                    .toList()
                )
                .todayPriceSeries(intradayValues.getOrDefault(ticker, new ArrayList<>())
                    .stream()
                    .map(intradayValue -> new TimeSeriesValue<>(
                        intradayValue.getPrice(),
                        intradayValue.getDateTime().toLocalTime()
                    ))
                    .toList())
                .build())
            .toList();
    }

    private ScannerEntity toEntity(SignalScanner dataScanner) {
        return mappers.get(dataScanner.getAlgorithm().getClass()).apply(dataScanner);
    }

    Map<Class<? extends ScannerAlgorithm>, Function<SignalScanner, ScannerEntity>> mappers = Map.of(
        AnomalyVolumeAlgorithm.class, AnomalyVolumeScannerEntity::from,
        SectoralRetardAlgorithm.class, SectoralRetardScannerEntity::from,
        CorrelationSectoralAlgorithm.class, CorrelationSectoralScannerEntity::from,
        PrefSimpleAlgorithm.class, PrefSimpleScannerEntity::from
    );
}
