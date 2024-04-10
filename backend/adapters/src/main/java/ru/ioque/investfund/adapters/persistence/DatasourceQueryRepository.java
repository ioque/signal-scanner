package ru.ioque.investfund.adapters.persistence;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.persistence.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.HistoryValueEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.IntradayValueEntity;
import ru.ioque.investfund.adapters.persistence.filter.InstrumentFilterParams;
import ru.ioque.investfund.adapters.persistence.repositories.JpaDatasourceRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaHistoryValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaIntradayValueRepository;
import ru.ioque.investfund.adapters.rest.ResourceNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Component
@AllArgsConstructor
public class DatasourceQueryRepository {
    JpaDatasourceRepository exchangeRepository;
    JpaInstrumentRepository instrumentRepository;
    JpaHistoryValueRepository historyValueRepository;
    JpaIntradayValueRepository intradayValueRepository;

    public List<DatasourceEntity> getAllDatasource() {
        return exchangeRepository.findAll();
    }

    public DatasourceEntity findDatasourceBy(UUID datasourceId) {
        return exchangeRepository
            .findById(datasourceId)
            .orElseThrow(notFoundException(notFoundDatasourceMsg()));
    }

    public InstrumentEntity findInstrumentBy(String ticker) {
        return instrumentRepository
            .findByTicker(ticker)
            .orElseThrow(notFoundException(instrumentNotFoundMsg()));
    }

    public List<HistoryValueEntity> findHistory(UUID datasourceId, InstrumentEntity instrument, LocalDate date) {
        return historyValueRepository.findAllBy(
            datasourceId,
            instrument.getTicker(),
            date
        );
    }

    public List<IntradayValueEntity> findIntraday(
        UUID datasourceId,
        InstrumentEntity instrument,
        LocalDateTime dateTime
    ) {
        return intradayValueRepository.findAllBy(datasourceId, instrument.getTicker(), dateTime);
    }

    public List<InstrumentEntity> findInstruments(InstrumentFilterParams filterParams) {
        if (filterParams.specificationIsEmpty() && filterParams.pageRequestIsEmpty()) return getAllInstruments();
        if (filterParams.specificationIsEmpty()) return findInstruments(filterParams.pageRequest());
        if (filterParams.pageRequestIsEmpty()) return findInstruments(filterParams.specification());
        return instrumentRepository.findAll(filterParams.specification(), filterParams.pageRequest()).toList();
    }

    public List<InstrumentEntity> findInstruments(Specification<InstrumentEntity> specification) {
        return instrumentRepository.findAll(specification);
    }

    public List<InstrumentEntity> findInstruments(PageRequest pageRequest) {
        return instrumentRepository.findAll(pageRequest).toList();
    }

    public List<InstrumentEntity> getAllInstruments() {
        return instrumentRepository.findAll();
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
