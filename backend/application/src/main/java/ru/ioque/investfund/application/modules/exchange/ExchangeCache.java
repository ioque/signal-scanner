package ru.ioque.investfund.application.modules.exchange;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.domain.exchange.entity.Exchange;

import java.util.Optional;

@Component
@NoArgsConstructor
public class ExchangeCache {
    Exchange exchange;

    public void put(Exchange exchange) {
        this.exchange = exchange;
    }

    public Optional<Exchange> get() {
        return Optional.ofNullable(exchange);
    }

    public void clear() {
        exchange = null;
    }
}
