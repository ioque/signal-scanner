package ru.ioque.investfund.fakes;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.domain.exchange.entity.Exchange;

import java.time.LocalDate;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class FakeDatasourceRepository implements DatasourceRepository {
    Exchange exchange;

    @Override
    public Optional<Exchange> getBy(LocalDate today) {
        return Optional.ofNullable(exchange);
    }

    @Override
    public void save(Exchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public void delete() {
        exchange = null;
    }
}