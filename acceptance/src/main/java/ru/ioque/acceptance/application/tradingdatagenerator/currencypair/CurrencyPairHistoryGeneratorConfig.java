package ru.ioque.acceptance.application.tradingdatagenerator.currencypair;

import lombok.Builder;
import ru.ioque.acceptance.application.tradingdatagenerator.core.GeneratorConfig;

public class CurrencyPairHistoryGeneratorConfig extends GeneratorConfig {
    @Builder
    public CurrencyPairHistoryGeneratorConfig(String ticker) {
        super(ticker);
    }
}
