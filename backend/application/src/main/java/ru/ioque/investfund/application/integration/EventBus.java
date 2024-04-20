package ru.ioque.investfund.application.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.integration.event.IntegrationEvent;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class EventBus {
    private final Map<Class<? extends IntegrationEvent>, EventHandler> handlers = new HashMap<>();

    @SuppressWarnings("unchecked")
    public EventBus(List<EventHandler<? extends IntegrationEvent>> handlers) {
        handlers.forEach(handler -> {
            final Class<? extends IntegrationEvent> eventClass =
                (Class<? extends IntegrationEvent>)
                    ((ParameterizedType) handler.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            this.handlers.put(eventClass, handler);
        });
    }

    @SuppressWarnings("unchecked")
    public void process(IntegrationEvent event) {
        Optional
            .ofNullable(handlers.get(event.getClass()))
            .ifPresentOrElse(
                handler -> handler.handleFor(event),
                () -> log.warn(String.format("Для события %s не существует обработчика.", event))
            );
    }

    public void handleFor(IntegrationEvent event) {

    }
}
