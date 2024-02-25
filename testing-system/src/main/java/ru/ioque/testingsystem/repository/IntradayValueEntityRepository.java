package ru.ioque.testingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ioque.testingsystem.entity.intradayvalue.IntradayValueEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IntradayValueEntityRepository extends JpaRepository<IntradayValueEntity, Long> {
    @Query("select d from IntradayValue d where d.dateTime >= :from and d.ticker = :ticker")
    List<IntradayValueEntity> findAllBy(String ticker, LocalDateTime from);

    @Query("select d.number from IntradayValue d where d.ticker = :ticker order by d.number DESC limit 1")
    Optional<Long> lastNumberBy(String ticker);
}
