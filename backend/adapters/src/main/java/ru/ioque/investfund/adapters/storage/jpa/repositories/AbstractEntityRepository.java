package ru.ioque.investfund.adapters.storage.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ioque.investfund.adapters.storage.jpa.entity.AbstractEntity;

import java.util.UUID;

public interface AbstractEntityRepository<T extends AbstractEntity> extends JpaRepository<T, UUID> {
}
