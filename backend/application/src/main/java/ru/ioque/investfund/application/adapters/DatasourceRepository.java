package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.indetity.DatasourceId;

import java.util.List;
import java.util.Optional;

public interface DatasourceRepository {
    List<Datasource> findAll();
    Optional<Datasource> findBy(DatasourceId datasourceId);
    void save(Datasource datasource);
    void remove(Datasource datasource);
    Datasource getById(DatasourceId datasourceId) throws EntityNotFoundException;
}