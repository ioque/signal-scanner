package ru.ioque.investfund.adapters.storage.jpa;

import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.StatisticRepository;
import ru.ioque.investfund.domain.statistic.InstrumentStatistic;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryStatisticRepository implements StatisticRepository {
    Map<UUID, InstrumentStatistic> statistics = new ConcurrentHashMap<>();
    @Override
    public void saveAll(List<InstrumentStatistic> statistics) {
        statistics.forEach(row -> this.statistics.put(row.getInstrumentId(), row));
    }

    @Override
    public InstrumentStatistic getBy(UUID instrumentId) {
        return statistics.get(instrumentId);
    }
}
