package ru.ioque.investfund.adapters.storage.jpa;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.historyvalue.HistoryValueEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.intradayvalue.IntradayValueEntity;
import ru.ioque.investfund.adapters.storage.jpa.filter.InstrumentFilterParams;
import ru.ioque.investfund.adapters.storage.jpa.repositories.HistoryValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.IntradayValueEntityRepository;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InstrumentQueryRepository {
    InstrumentEntityRepository instrumentEntityRepository;
    HistoryValueEntityRepository historyValueEntityRepository;
    IntradayValueEntityRepository intradayValueEntityRepository;

    public List<Instrument> getAll(InstrumentFilterParams filterParams) {
        if (filterParams.specificationIsEmpty() && filterParams.pageRequestIsEmpty()) return getAll();
        if (filterParams.specificationIsEmpty()) return getAll(filterParams.pageRequest());
        if (filterParams.pageRequestIsEmpty()) return getAll(filterParams.specification());
        return instrumentEntityRepository
            .findAll(filterParams.specification(), filterParams.pageRequest())
            .stream()
            .map(InstrumentEntity::toDomain)
            .toList();
    }

    private List<Instrument> getAll(Specification<InstrumentEntity> specification) {
        return instrumentEntityRepository
            .findAll(specification)
            .stream()
            .map(InstrumentEntity::toDomain)
            .toList();
    }

    private List<Instrument> getAll(PageRequest pageRequest) {
        return instrumentEntityRepository
            .findAll(pageRequest)
            .stream()
            .map(InstrumentEntity::toDomain)
            .toList();
    }

    public List<Instrument> getAll() {
        return instrumentEntityRepository
            .findAll()
            .stream()
            .map(InstrumentEntity::toDomain)
            .toList();
    }

    public Optional<Instrument> getById(UUID id) {
        return instrumentEntityRepository
            .findById(id)
            .map(InstrumentEntity::toDomain);
    }

    public Optional<Instrument> getWithTradingDataBy(UUID id, LocalDate today) {
        return instrumentEntityRepository
            .findById(id)
            .map(instrumentEntity -> instrumentEntity.toDomain(
                historyValueEntityRepository
                    .findAllBy(instrumentEntity.getTicker(), today.minusMonths(6))
                    .stream()
                    .map(HistoryValueEntity::toDomain)
                    .toList(),
                intradayValueEntityRepository
                    .findAllBy(List.of(instrumentEntity.getTicker()), today.atStartOfDay())
                    .stream()
                    .map(IntradayValueEntity::toDomain)
                    .toList()
            ));
    }
}
