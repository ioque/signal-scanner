package ru.ioque.investfund.adapters.storage.jpa;

import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.StatisticRepository;
import ru.ioque.investfund.domain.statistic.InstrumentStatistic;

import java.util.List;
import java.util.UUID;

@Component
public class JpaStatisticRepository implements StatisticRepository {
    @Override
    public void saveAll(List<InstrumentStatistic> statistics) {

    }

    @Override
    public InstrumentStatistic getBy(UUID instrumentId) {
        return null;
    }
}
