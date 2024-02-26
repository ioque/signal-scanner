package ru.ioque.investfund.adapters.storage.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.storage.jpa.entity.testingsystem.dailyvalue.ArchivedDailyValueEntity;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ArchivedDailyValueEntityRepository extends JpaRepository<ArchivedDailyValueEntity, Long> {
    @Query("select d.tradeDate from ArchivedDailyValue d order by d.tradeDate DESC limit 1")
    Optional<LocalDate> lastDate();

    @Procedure(procedureName = "archiving_daily_values")
    void archivingDailyValues();
}
