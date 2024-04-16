package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.entity.Datasource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DatasourceRepository {
    List<Datasource> getAll();
    Optional<Datasource> getBy(UUID datasourceId);
    void save(Datasource datasource);
    void remove(Datasource datasource);
}