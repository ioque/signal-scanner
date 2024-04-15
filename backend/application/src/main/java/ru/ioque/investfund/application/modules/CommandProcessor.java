package ru.ioque.investfund.application.modules;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.share.logger.ErrorLog;
import ru.ioque.investfund.application.share.logger.InfoLog;
import ru.ioque.investfund.application.share.logger.LoggerFacade;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
public abstract class CommandProcessor<C> {
    protected DateTimeProvider dateTimeProvider;
    protected Validator validator;
    protected LoggerFacade loggerFacade;

    public void handle(C command) {
        loggerFacade.log(new InfoLog(
            dateTimeProvider.nowDateTime(),
            String.format("Получена команда %s", command)
        ));
        validateCommand(command);
        handleFor(command);
    }

    protected abstract void handleFor(C command);

    protected void validateCommand(C command) {
        Set<ConstraintViolation<C>> violations = validator.validate(command);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    protected void executeBusinessProcess(Runnable runnable, String description) {
        long time = timeMeterWrapper(() -> tryWrapper(runnable));
        loggerFacade.log(
            new InfoLog(
                dateTimeProvider.nowDateTime(),
                String.format("%s, время выполнения составило %s мс", description, time)
            )
        );
    }

    private long timeMeterWrapper(Runnable runnable) {
        LocalDateTime start = dateTimeProvider.nowDateTime();
        runnable.run();
        return Duration.between(start, dateTimeProvider.nowDateTime()).toMillis();
    }

    private void tryWrapper(Runnable runnable) {
        try {
            runnable.run();
        } catch (RuntimeException e) {
            loggerFacade.log(
                new ErrorLog(dateTimeProvider.nowDateTime(), e.getMessage(), e.getCause())
            );
        }
    }
}
