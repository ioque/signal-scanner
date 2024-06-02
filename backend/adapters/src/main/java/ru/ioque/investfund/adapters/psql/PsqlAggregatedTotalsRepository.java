package ru.ioque.investfund.adapters.psql;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.psql.entity.datasource.aggregatedtotals.AggregatedTotalsEntity;
import ru.ioque.investfund.adapters.psql.dao.JpaAggregatedTotalsRepository;
import ru.ioque.investfund.application.adapters.repository.AggregatedTotalsRepository;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;

@Component
@AllArgsConstructor
public class PsqlAggregatedTotalsRepository implements AggregatedTotalsRepository {
    private final JpaAggregatedTotalsRepository dao;

    @Override
    @Transactional
    public void save(AggregatedTotals aggregatedTotals) {
        dao.save(AggregatedTotalsEntity.from(aggregatedTotals));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AggregatedTotals> findAllBy(InstrumentId instrumentId) {
        return dao.findAllBy(instrumentId.getUuid()).stream().map(AggregatedTotalsEntity::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AggregatedTotals> findActualBy(InstrumentId instrumentId) {
        return dao.findLastBy(instrumentId.getUuid()).map(AggregatedTotalsEntity::toDomain);
    }
}
