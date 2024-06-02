package ru.ioque.investfund.adapters.psql;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.psql.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.psql.dao.JpaInstrumentRepository;
import ru.ioque.investfund.application.adapters.repository.InstrumentRepository;
import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PsqlInstrumentRepository implements InstrumentRepository {
    JpaInstrumentRepository instrumentRepository;

    @Override
    @Transactional(readOnly = true)
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

    @Override
    public List<Instrument> findAllBy(List<InstrumentId> instrumentIds) {
        return List.of();
    }

    @Override
    public InstrumentId nextId() {
        return InstrumentId.from(UUID.randomUUID());
    }
}
