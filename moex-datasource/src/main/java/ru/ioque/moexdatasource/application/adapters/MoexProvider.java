package ru.ioque.moexdatasource.application.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import ru.ioque.moexdatasource.domain.instrument.Instrument;

public interface MoexProvider {

    JsonNode fetchInstruments(Class<? extends Instrument> stockClass);

    JsonNode fetchHistory(Instrument instrument);

    JsonNode fetchIntradayValues(Instrument instrument);
}
