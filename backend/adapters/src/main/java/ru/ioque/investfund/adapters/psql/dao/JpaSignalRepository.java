package ru.ioque.investfund.adapters.psql.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ioque.investfund.adapters.psql.entity.scanner.SignalEntity;

public interface JpaSignalRepository extends JpaRepository<SignalEntity, Long> {
}
