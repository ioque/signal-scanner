package ru.ioque.acceptance.application.tradingdatagenerator.currencypair;

import lombok.Builder;
import ru.ioque.acceptance.application.tradingdatagenerator.core.GeneratorConfig;

public class CurrencyPairTradeGeneratorConfig extends GeneratorConfig {
    @Builder
    public CurrencyPairTradeGeneratorConfig(String ticker) {
        super(ticker);
    }
}
