package ru.ioque.investfund.fakes;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.domain.exchange.entity.Exchange;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class FakeExchangeRepository implements ExchangeRepository {
    Exchange exchange;

    @Override
    public List<Exchange> getAllBy(LocalDate today) {
        if (exchange != null) return List.of(exchange);
        return List.of();
    }

    @Override
    public Optional<Exchange> getBy(UUID id, LocalDate today) {
        return Optional.ofNullable(exchange);
    }

    @Override
    public void save(Exchange exchange) {
        this.exchange = exchange;
    }

    public void clear() {
        exchange = null;
    }
}
