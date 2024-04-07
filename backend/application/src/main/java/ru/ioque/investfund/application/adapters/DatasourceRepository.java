package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.entity.Exchange;

import java.time.LocalDate;
import java.util.Optional;

public interface DatasourceRepository {
    Optional<Exchange> getBy(LocalDate today);
    void save(Exchange exchange);
    void delete();
}