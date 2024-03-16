package ru.ioque.investfund.adapters.storage.jpa;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.dailyvalue.DailyValueEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.intradayvalue.IntradayValueEntity;
import ru.ioque.investfund.adapters.storage.jpa.repositories.DailyValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.IntradayValueEntityRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.FinInstrumentRepository;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.DealResult;
import ru.ioque.investfund.domain.scanner.entity.FinInstrument;
import ru.ioque.investfund.domain.scanner.value.TimeSeriesValue;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class JpaFinInstrumentRepository implements FinInstrumentRepository {
    InstrumentEntityRepository instrumentEntityRepository;
    DailyValueEntityRepository dailyValueEntityRepository;
    IntradayValueEntityRepository intradayValueEntityRepository;

    DateTimeProvider dateTimeProvider;

    @Override
    public List<FinInstrument> getByIdIn(List<UUID> ids) {
        return ids
            .stream()
            .map(id -> {
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
}
