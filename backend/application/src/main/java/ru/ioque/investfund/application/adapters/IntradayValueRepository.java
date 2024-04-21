package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.value.intraday.IntradayValue;

import java.util.Collection;

public interface IntradayValueRepository {
    void saveAll(Collection<IntradayValue> intradayValues);
}
