package ru.ioque.investfund.application.modules.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class CommandBus {
    private final Map<Class<? extends Command>, CommandHandler> handlers = new HashMap<>();

    @SuppressWarnings("unchecked")
    public CommandBus(List<CommandHandler<? extends Command>> commandHandlers) {
        commandHandlers.forEach(commandProcessor -> {
            final Class<? extends Command> commandClass =
                (Class<? extends Command>)
                    ((ParameterizedType) commandProcessor.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            this.handlers.put(commandClass, commandProcessor);
        });
    }

    @SuppressWarnings("unchecked")
    public void execute(Command command) {
        Optional
            .ofNullable(handlers.get(command.getClass()))
            .ifPresentOrElse(
                handler -> handler.handleFor(UUID.randomUUID(), command),
                () -> log.warn(String.format("Для команды %s не существует обработчика.", command))
            );
    }
}
