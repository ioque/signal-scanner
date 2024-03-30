package ru.ioque.moexdatasource.adapters.repos;

import org.springframework.stereotype.Component;
import ru.ioque.moexdatasource.application.adapters.InstrumentRepo;
import ru.ioque.moexdatasource.domain.instrument.Instrument;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InstrumentRepoImpl implements InstrumentRepo {
    Map<String, Instrument> instruments = new ConcurrentHashMap<>();

    @Override
    public Optional<Instrument> findBy(String ticker) {
        return Optional.ofNullable(instruments.get(ticker));
    }

    @Override
    public List<Instrument> getAll() {
        return instruments.values().stream().toList();
    }

    @Override
    public void saveAll(List<Instrument> instruments) {
        instruments.forEach(instrument -> this.instruments.put(instrument.getTicker(), instrument));
    }
}
