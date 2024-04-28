package ru.ioque.investfund.adapters.persistence;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.application.adapters.InstrumentRepository;
import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PsqlInstrumentRepository implements InstrumentRepository {
    JpaInstrumentRepository instrumentRepository;

    @Override
    public Instrument getBy(InstrumentId instrumentId) throws EntityNotFoundException {
        return instrumentRepository
            .findById(instrumentId.getUuid())
            .map(InstrumentEntity::toDomain)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    String.format("Инструмент[id=%s] не существует.", instrumentId)
                )
            );
    }
}
