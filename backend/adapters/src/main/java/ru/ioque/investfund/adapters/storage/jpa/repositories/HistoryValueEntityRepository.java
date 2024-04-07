package ru.ioque.investfund.adapters.storage.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.historyvalue.HistoryValueEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryValueEntityRepository extends JpaRepository<HistoryValueEntity, Long> {
    @Query("select d from HistoryValue d where d.tradeDate >= :from and d.ticker = :ticker")
    List<HistoryValueEntity> findAllBy(String ticker, LocalDate from);

    @Query("select d.tradeDate from HistoryValue d where d.ticker = :ticker order by d.tradeDate DESC limit 1")
    Optional<LocalDate> lastDateBy(String ticker);

    List<HistoryValueEntity> findAllByTickerIn(List<String> tickers);
}
