package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.value.intraday.IntradayValue;

import java.util.List;

public interface IntradayValueRepository {
    void saveAll(List<IntradayValue> intradayValues);
}
