package ru.ioque.investfund.adapters.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.HistoryValueEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaHistoryValueRepository extends JpaRepository<HistoryValueEntity, Long> {
    @Query("select d from HistoryValue d where d.datasourceId = :datasourceId and d.ticker = :ticker and d.tradeDate >= :from")
    List<HistoryValueEntity> findAllBy(UUID datasourceId, String ticker, LocalDate from);

    List<HistoryValueEntity> findAllByDatasourceIdAndTickerIn(UUID datasourceId, List<String> tickers);
}
