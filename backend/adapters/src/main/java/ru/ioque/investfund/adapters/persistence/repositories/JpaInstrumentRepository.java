package ru.ioque.investfund.adapters.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaInstrumentRepository extends JpaRepository<InstrumentEntity, Long>, JpaSpecificationExecutor<InstrumentEntity> {

    List<InstrumentEntity> findAllByTickerIn(List<String> tickers);

    Optional<InstrumentEntity> findByTicker(String ticker);
}
