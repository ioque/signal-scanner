package ru.ioque.investfund.adapters.persistence;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.TradingStateEmbeddable;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.TradingSnapshotsRepository;
import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.value.TimeSeriesValue;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class PsqlTradingSnapshotsRepository implements TradingSnapshotsRepository {
    JpaInstrumentRepository jpaInstrumentRepository;
    DateTimeProvider dateTimeProvider;

    @Override
    @Transactional(readOnly = true)
    public List<TradingSnapshot> findAllBy(List<InstrumentId> instrumentIds) {
        final List<InstrumentEntity> instrumentEntities = jpaInstrumentRepository.findAllByIdIn(instrumentIds.stream().map(InstrumentId::getUuid).toList());
        return instrumentEntities
            .stream()
            .map(instrument -> buildSnapshot(instrument))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TradingSnapshot getBy(InstrumentId instrumentId) {
        final InstrumentEntity instrument = jpaInstrumentRepository
            .findById(instrumentId.getUuid())
            .orElseThrow(() -> new EntityNotFoundException(String.format("Инструмент[id=%s] не существует", instrumentId)));
        return buildSnapshot(instrument);
    }

    private static TradingSnapshot buildSnapshot(InstrumentEntity instrument) {
        return TradingSnapshot.builder()
            .instrumentId(InstrumentId.from(instrument.getId()))
            .ticker(Ticker.from(instrument.getTicker()))
            .dateTime(instrument.getTradingState().map(TradingStateEmbeddable::getDateTime).orElse(null))
            .firstPrice(instrument.getTradingState().map(TradingStateEmbeddable::getTodayFirstPrice).orElse(null))
            .lastPrice(instrument.getTradingState().map(TradingStateEmbeddable::getTodayLastPrice).orElse(null))
            .value(instrument.getTradingState().map(TradingStateEmbeddable::getTodayValue).orElse(null))
            .waPriceSeries(instrument.getHistory()
                .stream()
                .filter(row -> Objects.nonNull(row.getWaPrice()) && row.getWaPrice() > 0)
                .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getWaPrice(), dailyValue.getId().getDate()))
                .toList()
            )
            .closePriceSeries(instrument.getHistory()
                .stream()
                .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getClosePrice(), dailyValue.getId().getDate()))
                .toList())
            .openPriceSeries(instrument.getHistory()
                .stream()
                .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getOpenPrice(), dailyValue.getId().getDate()))
                .toList())
            .valueSeries(instrument.getHistory()
                .stream()
                .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getValue(), dailyValue.getId().getDate()))
                .toList())
            .build();
    }
}
