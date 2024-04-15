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

    protected abstract void handleFor(C command);

    public void handle(C command) {
        loggerFacade.log(new InfoLog(
            dateTimeProvider.nowDateTime(),
            String.format("Получена команда %s", command)
        ));
        validateCommand(command);
        execute(command);
    }

    private void validateCommand(C command) {
        Set<ConstraintViolation<C>> violations = validator.validate(command);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private void execute(C command) {
        try {
            long time = timeMeterWrapper(() -> handleFor(command));
            loggerFacade.log(
                new InfoLog(
                    dateTimeProvider.nowDateTime(),
                    String.format("Комада %s выполнена, время выполнения составило %s мс", command, time)
                )
            );
        } catch (Exception e) {
            loggerFacade.log(
                new ErrorLog(
                    dateTimeProvider.nowDateTime(),
                    String.format("Выполнение команды %s завершилось с ошибкой, текст ошибки: %s", command, e.getMessage()),
                    e.getCause())
            );
            throw e;
        }
    }

    private long timeMeterWrapper(Runnable runnable) {
        LocalDateTime start = dateTimeProvider.nowDateTime();
        runnable.run();
        return Duration.between(start, dateTimeProvider.nowDateTime()).toMillis();
    }
}
