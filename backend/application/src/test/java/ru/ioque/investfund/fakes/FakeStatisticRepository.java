package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.StatisticRepository;
import ru.ioque.investfund.domain.statistic.InstrumentStatistic;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FakeStatisticRepository implements StatisticRepository {
    Map<UUID, InstrumentStatistic> instrumentStatistics = new ConcurrentHashMap<>();
    @Override
    public void saveAll(List<InstrumentStatistic> statistics) {
        statistics.forEach(statistic -> instrumentStatistics.put(statistic.getInstrumentId(), statistic));
    }

    @Override
    public InstrumentStatistic getBy(UUID instrumentId) {
        return instrumentStatistics.get(instrumentId);
    }
}
