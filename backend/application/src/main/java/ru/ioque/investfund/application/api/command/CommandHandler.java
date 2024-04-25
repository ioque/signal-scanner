package ru.ioque.investfund.application.api.command;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.domain.core.ErrorLog;
import ru.ioque.investfund.domain.core.InfoLog;

import java.util.Set;

@AllArgsConstructor
public abstract class CommandHandler<C> {
    protected DateTimeProvider dateTimeProvider;
    protected Validator validator;
    protected LoggerProvider loggerProvider;
    protected UUIDProvider uuidProvider;

    public void handleFor(C command) {
        loggerProvider.log(new InfoLog(
            dateTimeProvider.nowDateTime(),
            String.format("Получена команда %s", command),
            ((Command) command).getTrack()
        ));
        validate(command);
        execute(command);
    }

    protected abstract void businessProcess(C command);

    protected <V> void validate(V value) {
        final Set<ConstraintViolation<V>> violations = validator.validate(value);
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
                    String.format("Комада %s успешно обработана, время выполнения составило %s мс", command, time),
                    ((Command) command).getTrack()
                )
            );
        } catch (Exception e) {
            loggerProvider.log(
                new ErrorLog(
                    dateTimeProvider.nowDateTime(),
                    String.format("При обработке команды %s произошла ошибка: %s", command, e.getMessage()),
                    e,
                    ((Command) command).getTrack()
                )
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
