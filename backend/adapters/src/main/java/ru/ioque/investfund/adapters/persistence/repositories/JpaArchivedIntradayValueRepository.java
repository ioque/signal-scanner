package ru.ioque.investfund.adapters.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.persistence.entity.archive.intradayvalue.ArchivedIntradayValue;

@Repository
public interface JpaArchivedIntradayValueRepository extends JpaRepository<ArchivedIntradayValue, Long> {

    @Procedure(procedureName = "archiving_intraday_values")
    void archivingIntradayValues();
}
