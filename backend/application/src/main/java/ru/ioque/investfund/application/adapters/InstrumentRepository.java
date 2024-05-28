package ru.ioque.investfund.application.adapters;

import java.util.List;

import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;

public interface InstrumentRepository {
   Instrument getBy(InstrumentId instrumentId) throws EntityNotFoundException;
   List<Instrument> findAllBy(List<InstrumentId> instrumentIds);
   InstrumentId nextId();
}
