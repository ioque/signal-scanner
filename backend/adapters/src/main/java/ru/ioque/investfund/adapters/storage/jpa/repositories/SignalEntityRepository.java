package ru.ioque.investfund.adapters.storage.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SignalEntity;

public interface SignalEntityRepository extends JpaRepository<SignalEntity, Long> {
}
