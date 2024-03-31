package ru.ioque.investfund.adapters.storage.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.storage.jpa.entity.archive.historyvalue.ArchivedHistoryValueEntity;

@Repository
public interface ArchivedHistoryValueEntityRepository extends JpaRepository<ArchivedHistoryValueEntity, Long> {

    @Procedure(procedureName = "archiving_history_values")
    void archivingHistoryValues();
}
