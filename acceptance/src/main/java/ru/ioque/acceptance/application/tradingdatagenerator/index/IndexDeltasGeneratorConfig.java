package ru.ioque.acceptance.application.tradingdatagenerator.index;

import lombok.Builder;
import ru.ioque.acceptance.application.tradingdatagenerator.core.GeneratorConfig;

public class IndexDeltasGeneratorConfig extends GeneratorConfig {
    @Builder
    public IndexDeltasGeneratorConfig(String ticker) {
        super(ticker);
    }
}
