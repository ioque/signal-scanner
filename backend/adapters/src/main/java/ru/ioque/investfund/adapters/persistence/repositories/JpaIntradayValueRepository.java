package ru.ioque.investfund.adapters.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.IntradayValueEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaIntradayValueRepository extends JpaRepository<IntradayValueEntity, Long> {
    @Query("select d from IntradayValue d where d.dateTime >= :from and d.ticker in :tickers")
    List<IntradayValueEntity> findAllBy(List<String> tickers, LocalDateTime from);

    @Query("select d from IntradayValue d where d.dateTime >= :from and d.ticker = :ticker")
    List<IntradayValueEntity> findAllBy(String ticker, LocalDateTime from);
}
