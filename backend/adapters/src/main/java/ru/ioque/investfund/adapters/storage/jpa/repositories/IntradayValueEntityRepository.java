package ru.ioque.investfund.adapters.storage.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.intradayvalue.IntradayValueEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IntradayValueEntityRepository extends JpaRepository<IntradayValueEntity, Long> {
    @Query("select d from IntradayValue d where d.dateTime >= :from and d.ticker = :ticker")
    List<IntradayValueEntity> findAllBy(String ticker, LocalDateTime from);
}
