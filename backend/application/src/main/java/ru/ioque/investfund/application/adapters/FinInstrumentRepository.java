package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.scanner.financial.entity.FinInstrument;

import java.util.List;
import java.util.UUID;

public interface FinInstrumentRepository {
    List<FinInstrument> getAllByInstrumentIdIn(List<UUID> instrumentIds);
}
