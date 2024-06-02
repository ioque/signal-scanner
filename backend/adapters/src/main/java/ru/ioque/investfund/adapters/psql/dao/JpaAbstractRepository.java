package ru.ioque.investfund.adapters.psql.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ioque.investfund.adapters.psql.entity.UuidIdentity;

import java.util.UUID;

public interface JpaAbstractRepository<T extends UuidIdentity> extends JpaRepository<T, UUID> {
}
