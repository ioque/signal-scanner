package ru.ioque.investfund.adapters.psql.dao;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.psql.entity.datasource.intradayvalue.IntradayDataEntity;
import ru.ioque.investfund.adapters.psql.entity.datasource.intradayvalue.IntradayPk;

@Repository
public interface JpaIntradayValueRepository extends JpaRepository<IntradayDataEntity, IntradayPk> {

    @Query("select i from IntradayData i where i.id.instrumentId = :instrumentId and i.timestamp between :from and :to")
    List<IntradayDataEntity> findAllBy(UUID instrumentId, Instant from, Instant to);

}
