package ru.ioque.acceptance.application.tradingdatagenerator.currencypair;

import ru.ioque.acceptance.application.tradingdatagenerator.core.HistoryGenerator;
import ru.ioque.acceptance.domain.dataemulator.currencyPair.CurrencyPairDailyResult;

import java.time.LocalDate;

public class CurrencyPairDailyResultGenerator extends HistoryGenerator<CurrencyPairDailyResult> {
    @Override
    public CurrencyPairDailyResult buildHistoryValue(
        String ticker,
        LocalDate date,
        double open,
        double close,
        double value,
        double volume
    ) {
        return CurrencyPairDailyResult.builder()
            .secId(ticker)
            .tradeDate(date)
            .open(open)
            .close(close)
            .volRur(value)
            .numTrades(volume)
            .build();
    }
}
