package ru.ioque.core.dataset;

import ru.ioque.core.datagenerator.history.HistoryValue;
import ru.ioque.core.datagenerator.instrument.Instrument;
import ru.ioque.core.datagenerator.intraday.IntradayValue;

import java.time.LocalDate;
import java.util.List;

public interface DatasetStorage {
    List<Instrument> getInstruments();
    List<IntradayValue> getIntradayValuesBy(String ticker, Integer start);
    List<HistoryValue> getHistoryValuesBy(String ticker, LocalDate from, LocalDate till);
}
