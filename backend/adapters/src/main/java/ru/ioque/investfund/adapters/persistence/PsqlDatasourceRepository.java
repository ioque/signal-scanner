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
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;

import java.util.Optional;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PsqlDatasourceRepository implements DatasourceRepository {
    JpaDatasourceRepository jpaDatasourceRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Datasource> findBy(DatasourceId datasourceId) {
        return jpaDatasourceRepository
            .findById(datasourceId.getUuid())
            .map(DatasourceEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Datasource getBy(DatasourceId datasourceId) throws EntityNotFoundException {
        return findBy(datasourceId).orElseThrow(
            () -> new EntityNotFoundException(
                String.format("Источник данных[id=%s] не существует.", datasourceId)
            )
        );
    }

    @Override
    @Transactional
    public void save(Datasource datasource) {
        jpaDatasourceRepository.save(DatasourceEntity.from(datasource));
    }

    @Override
    @Transactional
    public void remove(Datasource datasource) {
        jpaDatasourceRepository.deleteById(datasource.getId().getUuid());
    }
}
