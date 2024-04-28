package ru.ioque.investfund.fakes;

import lombok.AllArgsConstructor;
import ru.ioque.investfund.application.adapters.InstrumentRepository;
import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;

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
}
