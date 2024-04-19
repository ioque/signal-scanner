package ru.ioque.investfund.application.command;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.domain.core.ErrorLog;
import ru.ioque.investfund.domain.core.InfoLog;

import java.util.Set;

@AllArgsConstructor
public abstract class CommandHandler<C> {
    protected DateTimeProvider dateTimeProvider;
    protected Validator validator;
    protected LoggerProvider loggerProvider;

    public void handleFor(C command) {
        loggerProvider.log(new InfoLog(
            dateTimeProvider.nowDateTime(),
            String.format("Получена команда %s", command)
        ));
        validateCommand(command);
        execute(command);
    }

    protected abstract void businessProcess(C command);

    private void validateCommand(C command) {
        final Set<ConstraintViolation<C>> violations = validator.validate(command);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private void execute(C command) {
        try {
            final long time = timeMeterWrapper(() -> businessProcess(command));
            loggerProvider.log(
                new InfoLog(
                    dateTimeProvider.nowDateTime(),
                    String.format("Комада %s успешно обработана, время выполнения составило %s мс", command, time)
                )
            );
        } catch (Exception e) {
            loggerProvider.log(
                new ErrorLog(
                    dateTimeProvider.nowDateTime(),
                    String.format("При обработке команды %s произошла ошибка: %s", command, e.getMessage()),
                    e)
            );
            throw e;
        }
    }

    private long timeMeterWrapper(Runnable runnable) {
        final long start = System.currentTimeMillis();
        runnable.run();
        return System.currentTimeMillis() - start;
    }
}
