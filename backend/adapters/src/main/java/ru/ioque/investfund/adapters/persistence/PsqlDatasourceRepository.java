package ru.ioque.investfund.adapters.persistence;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.persistence.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaDatasourceRepository;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PsqlDatasourceRepository implements DatasourceRepository {
    JpaDatasourceRepository jpaDatasourceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Datasource> findAll() {
        return jpaDatasourceRepository
            .findAll()
            .stream()
            .map(DatasourceEntity::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Datasource> findBy(UUID datasourceId) {
        return jpaDatasourceRepository
            .findById(datasourceId)
            .map(DatasourceEntity::toDomain);
    }

    @Override
    @Transactional
    public void save(Datasource datasource) {
        jpaDatasourceRepository.save(DatasourceEntity.from(datasource));
    }

    @Override
    @Transactional
    public void remove(Datasource datasource) {
        jpaDatasourceRepository.deleteById(datasource.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Datasource getById(UUID datasourceId) throws EntityNotFoundException {
        return findBy(datasourceId).orElseThrow(
            () -> new EntityNotFoundException(
                String.format("Источник данных[id=%s] не существует.", datasourceId)
            )
        );
    }
}
