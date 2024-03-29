package ru.ioque.moexdatasource.adapters.providers;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import ru.ioque.moexdatasource.application.adapters.MoexProvider;
import ru.ioque.moexdatasource.domain.instrument.Instrument;

import java.util.Collection;
import java.util.List;

@Component
public class MoexProviderImpl implements MoexProvider {
    public List<JsonNode> fetch(Instrument instrument) {
        return List.of();
    }

    public Collection<? extends Instrument> fetchInstruments(Class<? extends Instrument> stockClass) {
        return List.of();
    }
}
