package ru.ioque.investfund.adapters.persistence.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.persistence.entity.datasource.aggregatedtotals.AggregatedTotalsEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.aggregatedtotals.AggregatedTotalsPk;

@Repository
public interface JpaAggregatedTotalsRepository extends JpaRepository<AggregatedTotalsEntity, AggregatedTotalsPk> {
    @Query("select i from AggregatedTotals i where i.id.instrumentId = :instrumentId")
    List<AggregatedTotalsEntity> findAllBy(UUID instrumentId);

    @Query("select i from AggregatedTotals i where i.id.instrumentId = :instrumentId order by i.id.date limit 1")
    Optional<AggregatedTotalsEntity> findLastBy(UUID instrumentId);
}
