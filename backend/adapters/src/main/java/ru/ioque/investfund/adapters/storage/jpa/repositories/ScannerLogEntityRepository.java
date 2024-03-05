package ru.ioque.investfund.adapters.storage.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ScannerLogEntity;

import java.util.List;
import java.util.UUID;

public interface ScannerLogEntityRepository extends JpaRepository<ScannerLogEntity, Long> {
    @Query("select r from ScannerLog r where r.scannerId = :scannerId")
    List<ScannerLogEntity> findAllByScannerId(UUID scannerId);
}
