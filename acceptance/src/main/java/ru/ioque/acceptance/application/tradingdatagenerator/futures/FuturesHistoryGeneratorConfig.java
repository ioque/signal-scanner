package ru.ioque.acceptance.application.tradingdatagenerator.futures;

import lombok.Builder;
import ru.ioque.acceptance.application.tradingdatagenerator.core.GeneratorConfig;

public class FuturesHistoryGeneratorConfig extends GeneratorConfig {
    @Builder
    public FuturesHistoryGeneratorConfig(String ticker) {
        super(ticker);
    }
}
