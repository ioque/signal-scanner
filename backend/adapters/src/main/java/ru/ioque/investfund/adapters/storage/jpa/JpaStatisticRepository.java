package ru.ioque.investfund.adapters.storage.jpa;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.storage.jpa.entity.statistic.InstrumentStatisticEntity;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentStatisticEntityRepository;
import ru.ioque.investfund.application.adapters.StatisticRepository;
import ru.ioque.investfund.domain.statistic.value.InstrumentStatistic;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class JpaStatisticRepository implements StatisticRepository {
    InstrumentStatisticEntityRepository statisticEntityRepository;
    @Override
    @Transactional
    public void saveAll(List<InstrumentStatistic> statistics) {
        statisticEntityRepository.saveAll(
            statistics.stream().map(InstrumentStatisticEntity::from).toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InstrumentStatistic> getBy(UUID instrumentId) {
        return statisticEntityRepository.findById(instrumentId).map(InstrumentStatisticEntity::toDomain);
    }
}
