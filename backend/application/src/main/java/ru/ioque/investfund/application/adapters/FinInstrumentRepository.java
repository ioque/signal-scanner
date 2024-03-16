package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.scanner.entity.FinInstrument;

import java.util.List;
import java.util.UUID;

public interface FinInstrumentRepository {
    List<FinInstrument> getByIdIn(List<UUID> ids);
}
