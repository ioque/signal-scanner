package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

import java.util.Collection;

public interface IntradayValueRepository {
    void saveAll(Collection<IntradayData> intradayData);

    void publish(IntradayData intradayData);
}
