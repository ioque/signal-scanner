package ru.ioque.acceptance.application.tradingdatagenerator.index;

import ru.ioque.acceptance.application.tradingdatagenerator.core.HistoryGenerator;
import ru.ioque.acceptance.domain.dataemulator.index.IndexDailyResult;

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
