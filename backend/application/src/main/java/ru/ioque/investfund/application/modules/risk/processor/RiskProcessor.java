package ru.ioque.investfund.application.modules.risk.processor;

import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.journal.Processor;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

@Component
public class RiskProcessor implements Processor<IntradayData> {
    @Override
    public void process(IntradayData intradayData) {

    }

    public boolean isInit() {
        return false;
    }
}
