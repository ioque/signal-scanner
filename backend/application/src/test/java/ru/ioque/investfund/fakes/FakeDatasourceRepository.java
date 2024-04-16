package ru.ioque.investfund.fakes;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FakeDatasourceRepository implements DatasourceRepository {
    final Map<UUID, Datasource> exchanges = new ConcurrentHashMap<>();

    @Override
    public List<Datasource> getAll() {
        return exchanges.values().stream().toList();
    }

    @Override
    public Optional<Datasource> getBy(UUID datasourceId) {
        return Optional.ofNullable(exchanges.get(datasourceId));
    }

    @Override
    public void save(Datasource datasource) {
        exchanges.put(datasource.getId(), datasource);
    }

    @Override
    public void remove(Datasource datasource) {
        exchanges.remove(datasource.getId());
    }
}