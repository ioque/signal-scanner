package ru.ioque.core.tradingdatagenerator.index;

import ru.ioque.core.dataemulator.index.IndexDailyResult;
import ru.ioque.core.tradingdatagenerator.core.HistoryGenerator;

import java.time.LocalDate;

public class IndexDailyResultGenerator extends HistoryGenerator<IndexDailyResult> {
    @Override
    public IndexDailyResult buildHistoryValue(
        String ticker,
        LocalDate date,
        double open,
        double close,
        double value,
        double volume
    ) {
        return IndexDailyResult.builder()
            .secId(ticker)
            .tradeDate(date)
            .open(open)
            .close(close)
            .value(value)
            .volume(volume)
            .build();
    }
}
