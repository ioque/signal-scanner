package ru.ioque.acceptance.application.tradingdatagenerator.futures;

import ru.ioque.acceptance.application.tradingdatagenerator.core.HistoryGenerator;
import ru.ioque.acceptance.domain.dataemulator.futures.FuturesDailyResult;

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
