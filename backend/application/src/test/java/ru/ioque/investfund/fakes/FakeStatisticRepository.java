package ru.ioque.investfund.fakes;

import lombok.Getter;
import ru.ioque.investfund.application.adapters.StatisticRepository;
import ru.ioque.investfund.domain.statistic.value.InstrumentStatistic;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class FakeStatisticRepository implements StatisticRepository {
    Map<UUID, InstrumentStatistic> instrumentStatistics = new ConcurrentHashMap<>();
    @Override
    public void saveAll(List<InstrumentStatistic> statistics) {
        statistics.forEach(statistic -> instrumentStatistics.put(statistic.getInstrumentId(), statistic));
    }

    @Override
    public Optional<InstrumentStatistic> getBy(UUID instrumentId) {
        return Optional.ofNullable(instrumentStatistics.get(instrumentId));
    }
}
