package ru.ioque.core.tradingdatagenerator.currencypair;

import ru.ioque.core.dataemulator.currencyPair.CurrencyPairDailyResult;
import ru.ioque.core.tradingdatagenerator.core.HistoryGenerator;

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
