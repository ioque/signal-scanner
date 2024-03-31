package ru.ioque.moexdatasource.application.adapters;

import ru.ioque.moexdatasource.domain.instrument.Instrument;

import java.util.List;
import java.util.Optional;

public interface InstrumentRepo {
    Optional<Instrument> findBy(String ticker);

    List<Instrument> getAll();

    void saveAll(List<Instrument> instruments);
}
