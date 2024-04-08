package ru.ioque.investfund.adapters.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ioque.investfund.adapters.persistence.entity.AbstractEntity;

import java.util.UUID;

public interface JpaAbstractRepository<T extends AbstractEntity> extends JpaRepository<T, UUID> {
}
