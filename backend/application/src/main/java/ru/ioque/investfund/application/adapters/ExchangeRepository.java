package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.exchange.entity.Exchange;

import java.time.LocalDate;
import java.util.Optional;

public interface ExchangeRepository {
    Optional<Exchange> get();
    /**
     * @param today текущая дата
     * @return стейт биржи на текущую дату
     */
    Optional<Exchange> getBy(LocalDate today);

    void save(Exchange exchange);
}
