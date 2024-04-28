package ru.ioque.investfund.application.api.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.UUIDProvider;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class CommandBus {
    private final Map<Class<? extends Command>, CommandHandler> handlers = new HashMap<>();
    private final UUIDProvider uuidProvider;
    @SuppressWarnings("unchecked")
    public CommandBus(List<CommandHandler<? extends Command>> commandHandlers, UUIDProvider uuidProvider) {
        commandHandlers.forEach(commandProcessor -> {
            final Class<? extends Command> commandClass =
                (Class<? extends Command>)
                    ((ParameterizedType) commandProcessor.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            this.handlers.put(commandClass, commandProcessor);
        });
        this.uuidProvider = uuidProvider;
    }

    @SuppressWarnings("unchecked")
    public void execute(Command command) {
        Optional
            .ofNullable(handlers.get(command.getClass()))
            .ifPresentOrElse(
                handler -> handler.handleFor(uuidProvider.generate(), command),
                () -> log.warn(String.format("Для команды %s не существует обработчика.", command))
            );
    }
}
