package ru.ioque.investfund.adapters.storage.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.storage.jpa.entity.testingsystem.intradayvalue.ArchivedIntradayValueEntity;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ArchivedIntradayValueEntityRepository extends JpaRepository<ArchivedIntradayValueEntity, Long> {

    @Procedure(procedureName = "archiving_intraday_values")
    void archivingIntradayValues();
}
