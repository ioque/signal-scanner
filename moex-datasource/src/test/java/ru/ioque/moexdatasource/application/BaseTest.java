package ru.ioque.moexdatasource.application;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.moexdatasource.adapters.repos.InstrumentRepoImpl;
import ru.ioque.moexdatasource.application.adapters.InstrumentRepo;
import ru.ioque.moexdatasource.application.fakes.FakeMoexProvider;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BaseTest {
    InstrumentRepo instrumentRepo = new InstrumentRepoImpl();
    FakeMoexProvider moexProvider = new FakeMoexProvider();
    InstrumentService instrumentService = new InstrumentService(instrumentRepo, moexProvider);
    TradingDataService tradingDataService = new TradingDataService(instrumentRepo, moexProvider);
}
