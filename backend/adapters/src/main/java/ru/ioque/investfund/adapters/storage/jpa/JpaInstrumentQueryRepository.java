package ru.ioque.investfund.adapters.storage.jpa;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.exception.AdapterException;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.dailyvalue.DailyValueEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.intradayvalue.IntradayValueEntity;
import ru.ioque.investfund.adapters.storage.jpa.repositories.DailyValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.IntradayValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.specification.FilterParams;
import ru.ioque.investfund.application.adapters.InstrumentQueryRepository;
import ru.ioque.investfund.domain.exchange.entity.Instrument;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JpaInstrumentQueryRepository implements InstrumentQueryRepository {
    InstrumentEntityRepository instrumentEntityRepository;
    DailyValueEntityRepository dailyValueEntityRepository;
    IntradayValueEntityRepository intradayValueEntityRepository;

    public List<Instrument> getBy(FilterParams filterParams) {
        return List.of();
    }

    @Override
    public List<Instrument> getAll() {
        return instrumentEntityRepository
            .findAll()
            .stream()
            .map(InstrumentEntity::toDomain)
            .toList();
    }

    @Override
    public Instrument getById(UUID id) {
        return instrumentEntityRepository
            .findById(id)
            .map(InstrumentEntity::toDomain)
            .orElseThrow(() -> new AdapterException("Инструмент с идентификатором " + id + " не найден."));
    }

    @Override
    public Instrument getWithTradingDataBy(UUID id, LocalDate today) {
        return instrumentEntityRepository
            .findById(id)
            .map(instrumentEntity -> instrumentEntity.toDomain(
                dailyValueEntityRepository
                    .findAllBy(instrumentEntity.getTicker(), today.minusMonths(6))
                    .stream()
                    .map(DailyValueEntity::toDomain)
                    .toList(),
                intradayValueEntityRepository
                    .findAllBy(instrumentEntity.getTicker(), today.atStartOfDay())
                    .stream()
                    .map(IntradayValueEntity::toDomain)
                    .toList()
            ))
            .orElseThrow(() -> new AdapterException("Инструмент с идентификатором " + id + " не найден."));
    }
}
