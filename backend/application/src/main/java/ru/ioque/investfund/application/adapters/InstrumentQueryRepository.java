package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.exchange.entity.Instrument;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface InstrumentQueryRepository {
    List<Instrument> getAll();
    Instrument getById(UUID id);
    Instrument getWithTradingDataBy(UUID id, LocalDate today);
}
