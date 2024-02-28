package ru.ioque.investfund.adapters.storage.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.storage.jpa.entity.testingsystem.dailyvalue.ArchivedDailyValueEntity;

@Repository
public interface ArchivedDailyValueEntityRepository extends JpaRepository<ArchivedDailyValueEntity, Long> {

    @Procedure(procedureName = "archiving_daily_values")
    void archivingDailyValues();
}
