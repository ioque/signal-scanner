package ru.ioque.investfund.adapters.storage.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.intradayvalue.IntradayValueEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IntradayValueRepository extends JpaRepository<IntradayValueEntity, Long> {
    @Query("select d from IntradayValue d where d.dateTime >= :from and d.ticker = :ticker")
    List<IntradayValueEntity> findAllBy(String ticker, LocalDateTime from);

    @Query("select d.number from IntradayValue d where d.ticker = :ticker order by d.number DESC limit 1")
    Optional<Long> lastNumberBy(String ticker);
}
