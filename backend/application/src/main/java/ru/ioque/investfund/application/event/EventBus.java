package ru.ioque.investfund.application.event;

import org.springframework.stereotype.Component;
import ru.ioque.investfund.domain.core.DomainEvent;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventBus {
    private final Map<Class<? extends DomainEvent>, EventHandler> handlers = new HashMap<>();

    @SuppressWarnings("unchecked")
    public EventBus(List<EventHandler<? extends DomainEvent>> handlers) {
        handlers.forEach(handler -> {
            final Class<? extends DomainEvent> eventClass =
                (Class<? extends DomainEvent>)
                    ((ParameterizedType) handler.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            this.handlers.put(eventClass, handler);
        });
    }

    @SuppressWarnings("unchecked")
    public void process(DomainEvent event) {
        handlers.get(event.getClass()).handleFor(event);
    }
}
