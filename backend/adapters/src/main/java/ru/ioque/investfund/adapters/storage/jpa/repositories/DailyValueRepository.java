package ru.ioque.investfund.adapters.storage.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.dailyvalue.DailyValueEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyValueRepository extends JpaRepository<DailyValueEntity, Long> {
    @Query("select d from DailyValue d where d.tradeDate >= :from and d.ticker = :ticker")
    List<DailyValueEntity> findAllBy(String ticker, LocalDate from);

    @Query("select d.tradeDate from DailyValue d where d.ticker = :ticker order by d.tradeDate DESC limit 1")
    Optional<LocalDate> lastDateBy(String ticker);
}
