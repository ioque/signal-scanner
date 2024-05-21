package ru.ioque.investfund.fakes;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.EventJournal;
import ru.ioque.investfund.application.integration.IntegrationEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Component
public class FakeEventJournal implements EventJournal {
    List<IntegrationEvent> events = new CopyOnWriteArrayList<>();

    @Override
    public void publish(IntegrationEvent event) {
        events.add(event);
    }
}
