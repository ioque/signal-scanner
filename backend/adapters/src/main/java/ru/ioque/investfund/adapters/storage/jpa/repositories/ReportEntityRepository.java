package ru.ioque.investfund.adapters.storage.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ReportEntity;

import java.util.List;
import java.util.UUID;

public interface ReportEntityRepository extends JpaRepository<ReportEntity, Long> {
    @Query("select r from Report r left join r.signals where r.scannerId = :scannerId")
    List<ReportEntity> findAllByScannerId(UUID scannerId);
}
