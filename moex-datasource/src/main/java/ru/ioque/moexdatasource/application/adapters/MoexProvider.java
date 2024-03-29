package ru.ioque.moexdatasource.application.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import ru.ioque.moexdatasource.domain.instrument.Instrument;

import java.util.Collection;
import java.util.List;

public interface MoexProvider {
    List<JsonNode> fetch(Instrument instrument);

    Collection<? extends Instrument> fetchInstruments(Class<? extends Instrument> stockClass);
}
