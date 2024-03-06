package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.statistic.InstrumentStatistic;

import java.util.List;

public interface StatisticRepository {
    void saveAll(List<InstrumentStatistic> statistics);
}
