package ru.ioque.investfund.adapters.integration;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.EventJournal;
import ru.ioque.investfund.application.integration.IntegrationEvent;

@Slf4j
@Getter
@Component
public class FakeEventJournal implements EventJournal {
    @Override
    public void publish(IntegrationEvent event) {
        log.info("Received event: {}", event);
    }
}
