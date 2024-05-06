package ru.ioque.investfund.adapters.query;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.persistence.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaDatasourceRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.query.filter.InstrumentFilterParams;
import ru.ioque.investfund.adapters.rest.Pagination;
import ru.ioque.investfund.domain.core.EntityNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Component
@AllArgsConstructor
public class PsqlDatasourceQueryService {
    JpaDatasourceRepository jpaDatasourceRepository;
    JpaInstrumentRepository jpaInstrumentRepository;

    public List<DatasourceEntity> getAllDatasource() {
        return jpaDatasourceRepository.findAll();
    }

    public DatasourceEntity findDatasourceBy(UUID datasourceId) {
        return jpaDatasourceRepository
            .findById(datasourceId)
            .orElseThrow(notFoundException(notFoundDatasourceMsg()));
    }

    public InstrumentEntity findInstrumentBy(UUID datasourceId, String ticker) {
        return jpaInstrumentRepository
            .findBy(datasourceId, ticker)
            .orElseThrow(notFoundException(instrumentNotFoundMsg()));
    }

    public Pagination<InstrumentEntity> getPagination(InstrumentFilterParams filterParams) {
        if (filterParams.pageRequestIsEmpty()) {
            return new Pagination<>(0,0, 0, List.of());
        }
        if (filterParams.specificationIsEmpty()) {
            final Page<InstrumentEntity> result = jpaInstrumentRepository.findAll(filterParams.pageRequest());
            return new Pagination<>(
                filterParams.pageRequest().getPageNumber(),
                result.getTotalPages(),
                result.getTotalElements(),
                result.getContent()
            );
        }
        final Page<InstrumentEntity> result = jpaInstrumentRepository.findAll(
            filterParams.specification(),
            filterParams.pageRequest()
        );
        return new Pagination<>(
            filterParams.pageRequest().getPageNumber(),
            result.getTotalPages(),
            result.getTotalElements(),
            result.getContent()
        );
    }

    private String instrumentNotFoundMsg() {
        return "Данные о финансовом инструменте не найдены.";
    }

    private String notFoundDatasourceMsg() {
        return "Источник данных не зарегистрирован.";
    }

    private Supplier<EntityNotFoundException> notFoundException(String message) {
        return () -> new EntityNotFoundException(message);
    }
}
