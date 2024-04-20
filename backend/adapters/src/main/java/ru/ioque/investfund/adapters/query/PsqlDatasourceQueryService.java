package ru.ioque.investfund.adapters.query;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.persistence.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.HistoryValueEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.IntradayValueEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaDatasourceRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaHistoryValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaIntradayValueRepository;
import ru.ioque.investfund.adapters.query.filter.InstrumentFilterParams;
import ru.ioque.investfund.adapters.rest.ResourceNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Component
@AllArgsConstructor
public class PsqlDatasourceQueryService {
    JpaDatasourceRepository jpaDatasourceRepository;
    JpaInstrumentRepository jpaInstrumentRepository;
    JpaHistoryValueRepository jpaHistoryValueRepository;
    JpaIntradayValueRepository jpaIntradayValueRepository;

    public List<DatasourceEntity> getAllDatasource() {
        return jpaDatasourceRepository.findAll();
    }

    public DatasourceEntity findDatasourceBy(UUID datasourceId) {
        return jpaDatasourceRepository
            .findById(datasourceId)
            .orElseThrow(notFoundException(notFoundDatasourceMsg()));
    }

    public InstrumentEntity findInstrumentBy(String ticker) {
        return jpaInstrumentRepository
            .findByTicker(ticker)
            .orElseThrow(notFoundException(instrumentNotFoundMsg()));
    }

    public List<HistoryValueEntity> findHistory(InstrumentEntity instrument, LocalDate date) {
        return jpaHistoryValueRepository.findAllBy(
            instrument.getTicker(),
            date
        );
    }

    public List<IntradayValueEntity> findIntraday(
        InstrumentEntity instrument,
        LocalDateTime dateTime
    ) {
        return jpaIntradayValueRepository.findAllBy(instrument.getTicker(), dateTime);
    }

    public List<InstrumentEntity> findInstruments(InstrumentFilterParams filterParams) {
        if (filterParams.specificationIsEmpty() && filterParams.pageRequestIsEmpty()) return getAllInstruments();
        if (filterParams.specificationIsEmpty()) return findInstruments(filterParams.pageRequest());
        if (filterParams.pageRequestIsEmpty()) return findInstruments(filterParams.specification());
        return jpaInstrumentRepository.findAll(filterParams.specification(), filterParams.pageRequest()).toList();
    }

    public List<InstrumentEntity> findInstruments(Specification<InstrumentEntity> specification) {
        return jpaInstrumentRepository.findAll(specification);
    }

    public List<InstrumentEntity> findInstruments(PageRequest pageRequest) {
        return jpaInstrumentRepository.findAll(pageRequest).toList();
    }

    public List<InstrumentEntity> getAllInstruments() {
        return jpaInstrumentRepository.findAll();
    }

    private String instrumentNotFoundMsg() {
        return "Данные о финансовом инструменте не найдены.";
    }

    private String notFoundDatasourceMsg() {
        return "Источник данных не зарегистрирована.";
    }

    private Supplier<ResourceNotFoundException> notFoundException(String message) {
        return () -> new ResourceNotFoundException(message);
    }
}
