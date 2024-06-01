package ru.ioque.investfund.adapters.persistence.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaInstrumentRepository extends JpaAbstractRepository<InstrumentEntity>, JpaSpecificationExecutor<InstrumentEntity> {
    List<InstrumentEntity> findAllByIdIn(List<UUID> ids);

    @Query("select i from Instrument i where i.datasource.id = :datasourceId and i.ticker = :ticker")
    Optional<InstrumentEntity> findBy(UUID datasourceId, String ticker);
}
