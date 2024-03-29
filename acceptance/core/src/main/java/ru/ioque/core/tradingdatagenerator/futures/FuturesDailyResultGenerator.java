package ru.ioque.core.tradingdatagenerator.futures;

import ru.ioque.core.dataemulator.futures.FuturesDailyResult;
import ru.ioque.core.tradingdatagenerator.core.HistoryGenerator;

import java.time.LocalDate;

public class FuturesDailyResultGenerator extends HistoryGenerator<FuturesDailyResult> {
    @Override
    public FuturesDailyResult buildHistoryValue(
        String ticker,
        LocalDate date,
        double open,
        double close,
        double value,
        double volume
    ) {
        return FuturesDailyResult.builder()
            .secId(ticker)
            .tradeDate(date)
            .open(open)
            .close(close)
            .value(value)
            .volume((int) volume)
            .build();
    }
}
