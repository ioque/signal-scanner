package ru.ioque.investfund.adapters.rest;

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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Component
@AllArgsConstructor
public class DatasourceQueryService {
    JpaDatasourceRepository exchangeRepository;
    JpaInstrumentRepository instrumentRepository;
    JpaHistoryValueRepository historyValueRepository;
    JpaIntradayValueRepository intradayValueRepository;

    public DatasourceEntity findDatasource() {
        return exchangeRepository
            .findAll()
            .stream()
            .findFirst()
            .orElseThrow(notFoundException(notFoundDatasourceMsg()));
    }

    public InstrumentEntity findInstrumentBy(UUID id) {
        return instrumentRepository
            .findById(id)
            .orElseThrow(notFoundException(instrumentNotFoundMsg()));
    }

    public List<HistoryValueEntity> findHistory(InstrumentEntity instrument, LocalDate date) {
        return historyValueRepository.findAllBy(
            instrument.getTicker(),
            date
        );
    }

    public List<IntradayValueEntity> findIntraday(InstrumentEntity instrument, LocalDateTime dateTime) {
        return intradayValueRepository.findAllBy(instrument.getTicker(), dateTime);
    }

    public List<InstrumentEntity> findInstruments(InstrumentFilterParams filterParams) {
        if (filterParams.specificationIsEmpty() && filterParams.pageRequestIsEmpty()) return getAllInstruments();
        if (filterParams.specificationIsEmpty()) return findInstruments(filterParams.pageRequest());
        if (filterParams.pageRequestIsEmpty()) return findInstruments(filterParams.specification());
        return instrumentRepository.findAll(filterParams.specification(), filterParams.pageRequest()).toList();
    }

    private List<InstrumentEntity> findInstruments(Specification<InstrumentEntity> specification) {
        return instrumentRepository.findAll(specification);
    }

    private List<InstrumentEntity> findInstruments(PageRequest pageRequest) {
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
