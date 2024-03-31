package ru.ioque.investfund.adapters.storage.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.storage.jpa.entity.archive.intradayvalue.ArchivedIntradayValueEntity;

@Repository
public interface ArchivedIntradayValueEntityRepository extends JpaRepository<ArchivedIntradayValueEntity, Long> {

    @Procedure(procedureName = "archiving_intraday_values")
    void archivingIntradayValues();
}
