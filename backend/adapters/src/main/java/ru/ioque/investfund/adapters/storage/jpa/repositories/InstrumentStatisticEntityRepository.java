package ru.ioque.investfund.adapters.storage.jpa.repositories;

import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.storage.jpa.entity.statistic.InstrumentStatisticEntity;

@Repository
public interface InstrumentStatisticEntityRepository extends AbstractEntityRepository<InstrumentStatisticEntity> {
}
