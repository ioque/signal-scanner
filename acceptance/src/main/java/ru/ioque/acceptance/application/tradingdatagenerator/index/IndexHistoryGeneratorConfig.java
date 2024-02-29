package ru.ioque.acceptance.application.tradingdatagenerator.index;

import lombok.Builder;
import ru.ioque.acceptance.application.tradingdatagenerator.core.GeneratorConfig;

public class IndexHistoryGeneratorConfig extends GeneratorConfig {
    @Builder
    public IndexHistoryGeneratorConfig(String ticker) {
        super(ticker);
    }
}
