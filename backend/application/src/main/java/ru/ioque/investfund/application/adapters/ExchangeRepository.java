package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.exchange.entity.Exchange;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExchangeRepository {
    List<Exchange> getAllBy(LocalDate today);
    Optional<Exchange> getBy(UUID id, LocalDate today);
    void save(Exchange exchange);
}
