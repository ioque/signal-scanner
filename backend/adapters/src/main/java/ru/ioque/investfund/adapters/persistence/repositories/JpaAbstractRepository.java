package ru.ioque.investfund.adapters.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ioque.investfund.adapters.persistence.entity.UuidIdentity;

import java.util.UUID;

public interface JpaAbstractRepository<T extends UuidIdentity> extends JpaRepository<T, UUID> {
}
