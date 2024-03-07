package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.statistic.InstrumentStatistic;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StatisticRepository {
    void saveAll(List<InstrumentStatistic> statistics);
    Optional<InstrumentStatistic> getBy(UUID instrumentId);
}
