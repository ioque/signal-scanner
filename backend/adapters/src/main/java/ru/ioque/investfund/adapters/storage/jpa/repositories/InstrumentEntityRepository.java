package ru.ioque.investfund.adapters.storage.jpa.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.InstrumentEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstrumentEntityRepository extends AbstractEntityRepository<InstrumentEntity>,
    JpaSpecificationExecutor<InstrumentEntity> {

    Optional<InstrumentEntity> findByTicker(String ticker);
    List<InstrumentEntity> findAllByTickerIn(List<String> tickers);
}
