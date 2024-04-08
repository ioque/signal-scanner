package ru.ioque.investfund.adapters.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.HistoryValueEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JpaHistoryValueRepository extends JpaRepository<HistoryValueEntity, Long> {
    @Query("select d from HistoryValue d where d.tradeDate >= :from and d.ticker = :ticker")
    List<HistoryValueEntity> findAllBy(String ticker, LocalDate from);

    List<HistoryValueEntity> findAllByTickerIn(List<String> tickers);
}
