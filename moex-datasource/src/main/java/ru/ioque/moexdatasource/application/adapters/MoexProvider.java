package ru.ioque.moexdatasource.application.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import ru.ioque.moexdatasource.domain.instrument.Instrument;

import java.time.LocalDate;
import java.util.List;

public interface MoexProvider {

    List<JsonNode> fetchInstruments(Class<? extends Instrument> stockClass);

    List<JsonNode> fetchHistory(
        Instrument instrument,
        LocalDate from,
        LocalDate to
    );

    List<JsonNode> fetchIntradayValues(Instrument instrument, long lastNumber);
}
