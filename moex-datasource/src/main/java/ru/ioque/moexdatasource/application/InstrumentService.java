package ru.ioque.moexdatasource.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.moexdatasource.application.adapters.InstrumentRepo;
import ru.ioque.moexdatasource.application.adapters.MoexProvider;
import ru.ioque.moexdatasource.domain.instrument.CurrencyPair;
import ru.ioque.moexdatasource.domain.instrument.Futures;
import ru.ioque.moexdatasource.domain.instrument.Index;
import ru.ioque.moexdatasource.domain.instrument.Instrument;
import ru.ioque.moexdatasource.domain.instrument.Stock;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class InstrumentService {
    InstrumentRepo instrumentRepo;
    MoexProvider moexProvider;

    public List<Instrument> getInstruments() {
        return instrumentRepo.getAll();
    }

    public void updateInstruments() {
        List<Instrument> instruments = new ArrayList<>();
        instruments.addAll(moexProvider.fetchInstruments(Stock.class));
        instruments.addAll(moexProvider.fetchInstruments(CurrencyPair.class));
        instruments.addAll(moexProvider.fetchInstruments(Futures.class));
        instruments.addAll(moexProvider.fetchInstruments(Index.class));
        instrumentRepo.saveAll(instruments);
    }
}
