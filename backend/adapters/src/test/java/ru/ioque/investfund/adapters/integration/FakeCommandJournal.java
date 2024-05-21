package ru.ioque.investfund.adapters.integration;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.CommandJournal;
import ru.ioque.investfund.application.modules.api.Command;

@Slf4j
@Getter
@Component
public class FakeCommandJournal implements CommandJournal {
    @Override
    public void publish(Command command) {
        log.info("Received command: {}", command);
    }
}
