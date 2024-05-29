package ru.ioque.investfund.application.adapters.repository;

import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;

import java.util.Optional;

public interface DatasourceRepository {
    Optional<Datasource> findBy(DatasourceId datasourceId);
    void save(Datasource datasource);
    void remove(Datasource datasource);
    Datasource getBy(DatasourceId datasourceId) throws EntityNotFoundException;
    DatasourceId nextId();
}