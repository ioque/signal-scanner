package ru.ioque.investfund.application.adapters.journal;

import ru.ioque.investfund.application.modules.pipeline.core.Source;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

public interface IntradayJournal extends Source<IntradayData> {
    void publish(IntradayData intradayData);
}
