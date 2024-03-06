package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.statistic.InstrumentStatistic;

import java.util.List;
import java.util.UUID;

public interface StatisticRepository {
    void saveAll(List<InstrumentStatistic> statistics);
    InstrumentStatistic getBy(UUID instrumentId);
}
