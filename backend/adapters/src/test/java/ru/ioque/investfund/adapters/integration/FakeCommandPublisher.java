package ru.ioque.investfund.adapters.integration;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.CommandPublisher;
import ru.ioque.investfund.domain.core.Command;

@Slf4j
@Getter
@Component
public class FakeCommandPublisher implements CommandPublisher {
    @Override
    public void publish(Command command) {
        log.info("Received command: {}", command);
    }
}
