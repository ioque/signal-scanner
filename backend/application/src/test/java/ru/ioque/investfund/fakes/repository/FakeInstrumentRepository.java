package ru.ioque.investfund.fakes.repository;

import lombok.AllArgsConstructor;
import ru.ioque.investfund.application.adapters.repository.InstrumentRepository;
import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class FakeInstrumentRepository implements InstrumentRepository {
    FakeDatasourceRepository datasourceRepository;

    @Override
    public Instrument getBy(InstrumentId instrumentId) throws EntityNotFoundException {
        return datasourceRepository
            .findInstrumentBy(instrumentId)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    String.format("Инструмент[id=%s] не существует.", instrumentId)
                )
            );
    }

    @Override
    public List<Instrument> findAllBy(List<InstrumentId> instrumentIds) {
        return instrumentIds.stream().map(this::getBy).toList();
    }

    @Override
    public InstrumentId nextId() {
        return InstrumentId.from(UUID.randomUUID());
    }
}
