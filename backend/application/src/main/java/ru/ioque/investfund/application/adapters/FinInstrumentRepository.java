package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.scanner.entity.FinInstrument;

import java.util.List;

public interface FinInstrumentRepository {
    List<FinInstrument> getBy(List<String> tickers);
}
