package ru.ioque.investfund.application.modules;

import org.springframework.stereotype.Component;
import ru.ioque.investfund.domain.core.Command;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommandBus {
    Map<Class<? extends Command>, CommandHandler> commandProcessors = new HashMap<>();

    @SuppressWarnings("unchecked")
    public CommandBus(List<CommandHandler<? extends Command>> commandHandlers) {
        commandHandlers.forEach(commandProcessor -> {
            final Class<? extends Command> commandClass =
                (Class<? extends Command>)
                    ((ParameterizedType) commandProcessor.getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];
            this.commandProcessors.put(commandClass, commandProcessor);
        });
    }

    @SuppressWarnings("unchecked")
    public void execute(Command command) {
        commandProcessors.get(command.getClass()).handle(command);
    }
}
