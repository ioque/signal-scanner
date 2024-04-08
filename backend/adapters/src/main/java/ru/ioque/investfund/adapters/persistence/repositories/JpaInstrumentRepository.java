package ru.ioque.investfund.adapters.persistence.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;

import java.util.List;

@Repository
public interface JpaInstrumentRepository extends JpaAbstractRepository<InstrumentEntity>,
    JpaSpecificationExecutor<InstrumentEntity> {

    List<InstrumentEntity> findAllByTickerIn(List<String> tickers);
}
