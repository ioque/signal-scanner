package ru.ioque.investfund.adapters.storage.jpa;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import ru.ioque.investfund.application.adapters.EventPublisher;

import static org.mockito.Mockito.mock;

@Order(1)
@TestConfiguration
public class JpaTestTestConfiguration {

    @Bean
    @Primary
    public EventPublisher mockEventPublisher() {
        return mock(EventPublisher.class);
    }
}
