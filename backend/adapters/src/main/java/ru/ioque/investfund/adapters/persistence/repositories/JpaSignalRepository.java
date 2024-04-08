package ru.ioque.investfund.adapters.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ioque.investfund.adapters.persistence.entity.scanner.SignalEntity;

public interface JpaSignalRepository extends JpaRepository<SignalEntity, Long> {
}
