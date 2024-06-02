package ru.ioque.investfund.application.adapters;

import java.time.Instant;
import java.util.List;

import ru.ioque.investfund.application.modules.pipeline.core.Source;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

public interface IntradayDataJournal extends Source<IntradayData> {
    List<IntradayData> findAllBy(InstrumentId instrumentId, Instant from, Instant to);
}
