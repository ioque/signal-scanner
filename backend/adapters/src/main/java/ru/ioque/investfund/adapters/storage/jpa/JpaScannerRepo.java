package ru.ioque.investfund.adapters.storage.jpa;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.dailyvalue.DailyValueEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.intradayvalue.IntradayValueEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.AnomalyVolumeScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.CorrelationSectoralScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.PrefSimpleScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SectoralRetardScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SignalScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.statistic.InstrumentStatisticEntity;
import ru.ioque.investfund.adapters.storage.jpa.repositories.DailyValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentStatisticEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.IntradayValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.SignalScannerEntityRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.DealResult;
import ru.ioque.investfund.domain.scanner.algorithms.AnomalyVolumeSignalConfig;
import ru.ioque.investfund.domain.scanner.algorithms.CorrelationSectoralSignalConfig;
import ru.ioque.investfund.domain.scanner.algorithms.PrefSimpleSignalConfig;
import ru.ioque.investfund.domain.scanner.algorithms.SectoralRetardSignalConfig;
import ru.ioque.investfund.domain.scanner.entity.FinInstrument;
import ru.ioque.investfund.domain.scanner.entity.SignalConfig;
import ru.ioque.investfund.domain.scanner.entity.SignalScannerBot;
import ru.ioque.investfund.domain.scanner.value.TimeSeriesValue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JpaScannerRepo implements ScannerRepository {
    SignalScannerEntityRepository signalScannerEntityRepository;
    InstrumentEntityRepository instrumentEntityRepository;
    DailyValueEntityRepository dailyValueEntityRepository;
    IntradayValueEntityRepository intradayValueEntityRepository;
    InstrumentStatisticEntityRepository instrumentStatisticEntityRepository;
    DateTimeProvider dateTimeProvider;

    @Override
    @Transactional(readOnly = true)
    public Optional<SignalScannerBot> getBy(UUID id) {
        return signalScannerEntityRepository
            .findById(id)
            .map(row -> row.toDomain(getFinInstrumentsByIds(row.getObjectIds())));
    }

    @Override
    @Transactional
    public void save(SignalScannerBot dataScanner) {
        signalScannerEntityRepository.save(toEntity(dataScanner));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SignalScannerBot> getAll() {
        return signalScannerEntityRepository
            .findAll()
            .stream()
            .map(row -> row.toDomain(getFinInstrumentsByIds(row.getObjectIds())))
            .toList();
    }

    private List<FinInstrument> getFinInstrumentsByIds(List<UUID> objectIds) {
        return objectIds.stream().map(id -> {
                Instrument instrument = instrumentEntityRepository
                    .findById(id)
                    .map(entity ->
                        entity.toDomain(
                            dailyValueEntityRepository
                                .findAllBy(entity.getTicker(), dateTimeProvider.nowDate().minusMonths(6))
                                .stream()
                                .map(DailyValueEntity::toDomain)
                                .toList(),
                            intradayValueEntityRepository
                                .findAllBy(entity.getTicker(), dateTimeProvider.nowDate().atStartOfDay())
                                .stream()
                                .map(IntradayValueEntity::toDomain)
                                .toList()
                        )
                    )
                    .orElseThrow();
                Optional<InstrumentStatisticEntity> statistic = instrumentStatisticEntityRepository.findById(id);
                return FinInstrument.builder()
                    .instrumentId(instrument.getId())
                    .ticker(instrument.getTicker())
                    .historyMedianValue(statistic.map(InstrumentStatisticEntity::getHistoryMedianValue).orElse(0.0))
                    .todayLastPrice(statistic.map(InstrumentStatisticEntity::getTodayLastPrice).orElse(0.0))
                    .todayOpenPrice(statistic.map(InstrumentStatisticEntity::getTodayOpenPrice).orElse(0.0))
                    .todayValue(statistic.map(InstrumentStatisticEntity::getTodayValue).orElse(0.0))
                    .buyToSellValuesRatio(statistic.map(InstrumentStatisticEntity::getBuyToSellValuesRatio).orElse(0.0))
                    .waPriceSeries(instrument
                        .getDailyValues()
                        .stream()
                        .filter(row -> row.getClass().equals(DealResult.class))
                        .map(DealResult.class::cast)
                        .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getWaPrice(), dailyValue.getTradeDate()))
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
                    .todayPriceSeries(instrument
                        .getIntradayValues()
                        .stream()
                        .map(intradayValue -> new TimeSeriesValue<>(
                            intradayValue.getPrice(),
                            intradayValue.getDateTime().toLocalTime()
                        ))
                        .toList())
                    .build();
            })
            .toList();
    }

    private SignalScannerEntity toEntity(SignalScannerBot dataScanner) {
        return mappers.get(dataScanner.getConfig().getClass()).apply(dataScanner);
    }

    Map<Class<? extends SignalConfig>, Function<SignalScannerBot, SignalScannerEntity>> mappers = Map.of(
        AnomalyVolumeSignalConfig.class, AnomalyVolumeScannerEntity::from,
        SectoralRetardSignalConfig.class, SectoralRetardScannerEntity::from,
        CorrelationSectoralSignalConfig.class, CorrelationSectoralScannerEntity::from,
        PrefSimpleSignalConfig.class, PrefSimpleScannerEntity::from
    );
}
