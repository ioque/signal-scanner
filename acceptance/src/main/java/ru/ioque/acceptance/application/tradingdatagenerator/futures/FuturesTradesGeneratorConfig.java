package ru.ioque.acceptance.application.tradingdatagenerator.futures;

import lombok.Builder;
import ru.ioque.acceptance.application.tradingdatagenerator.core.GeneratorConfig;

public class FuturesTradesGeneratorConfig extends GeneratorConfig {
    @Builder
    public FuturesTradesGeneratorConfig(String ticker) {
        super(ticker);
    }
}
