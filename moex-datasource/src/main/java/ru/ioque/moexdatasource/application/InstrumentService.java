package ru.ioque.moexdatasource.application;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.moexdatasource.application.adapters.InstrumentRepo;
import ru.ioque.moexdatasource.application.adapters.MoexProvider;
import ru.ioque.moexdatasource.domain.instrument.CurrencyPair;
import ru.ioque.moexdatasource.domain.instrument.Futures;
import ru.ioque.moexdatasource.domain.instrument.Index;
import ru.ioque.moexdatasource.domain.instrument.Instrument;
import ru.ioque.moexdatasource.domain.instrument.Stock;
import ru.ioque.moexdatasource.domain.parser.InstrumentParser;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InstrumentService {
    InstrumentParser instrumentParser = new InstrumentParser();
    InstrumentRepo instrumentRepo;
    MoexProvider moexProvider;

    public List<Instrument> getInstruments() {
        if (instrumentRepo.currentSize() == 0) {
            downloadInstruments();
        }
        return instrumentRepo.getAll();
    }

    public void downloadInstruments() {
        List<Instrument> instruments = new ArrayList<>();
        instruments.addAll(
            instrumentParser
                .parse(
                    moexProvider
                        .fetchInstruments(Stock.class), Stock.class)
        );
        instruments.addAll(
            instrumentParser
                .parse(
                    moexProvider.fetchInstruments(CurrencyPair.class),
                    CurrencyPair.class
                )
        );
        instruments.addAll(
            instrumentParser
                .parse(
                    moexProvider.fetchInstruments(Futures.class),
                    Futures.class)
        );
        instruments.addAll(
            instrumentParser
                .parse(
                    moexProvider.fetchInstruments(Index.class),
                    Index.class)
        );
        instrumentRepo.saveAll(instruments);
    }
}
