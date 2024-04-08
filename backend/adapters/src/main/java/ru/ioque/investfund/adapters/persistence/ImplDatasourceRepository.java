package ru.ioque.investfund.adapters.persistence;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.persistence.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.HistoryValueEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.IntradayValueEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaDatasourceRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaHistoryValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaIntradayValueRepository;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ImplDatasourceRepository implements DatasourceRepository {
    JpaDatasourceRepository exchangeRepository;
    JpaHistoryValueRepository jpaHistoryValueRepository;
    JpaIntradayValueRepository jpaIntradayValueRepository;

    @Override
    public List<Datasource> getAll() {
        return exchangeRepository
            .findAll()
            .stream()
            .map(DatasourceEntity::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Datasource> getBy(UUID datasourceId) {
        return exchangeRepository
            .findById(datasourceId)
            .map(DatasourceEntity::toDomain);
    }

    @Override
    @Transactional
    public void saveDatasource(Datasource datasource) {
        exchangeRepository.save(DatasourceEntity.fromDomain(datasource));
    }

    @Override
    public void saveIntradayValues(List<IntradayValue> intradayValues) {
        jpaIntradayValueRepository.saveAll(intradayValues.stream().map(IntradayValueEntity::fromDomain).toList());
    }

    @Override
    public void saveHistoryValues(List<HistoryValue> historyValues) {
        jpaHistoryValueRepository.saveAll(historyValues.stream().map(HistoryValueEntity::fromDomain).toList());
    }

    @Override
    @Transactional
    public void deleteDatasource(UUID datasourceId) {
        jpaIntradayValueRepository.deleteAll();
        jpaHistoryValueRepository.deleteAll();
        exchangeRepository.deleteById(datasourceId);
    }
}
