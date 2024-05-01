package ru.ioque.investfund.application.api.command;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.domain.core.ErrorLog;
import ru.ioque.investfund.domain.core.InfoLog;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class CommandHandler<C> {
    DateTimeProvider dateTimeProvider;
    Validator validator;
    LoggerProvider loggerProvider;

    public void handleFor(UUID trackId, C command) {
        try {
            loggerProvider.log(
                trackId,
                new InfoLog(timestamp(), String.format("Получена команда %s", command))
            );
            final long start = System.currentTimeMillis();
            final Result result = execute(command);
            final long duration = System.currentTimeMillis() - start;
            result.getLogs().forEach(applicationLog -> loggerProvider.log(trackId, applicationLog));
            if (result.isSuccess()) {
                loggerProvider.log(
                    trackId,
                    new InfoLog(
                        timestamp(),
                        String.format(
                            "Комада %s успешно обработана, время выполнения составило %s мс",
                            command,
                            duration
                        )
                    )
                );
            } else {
                loggerProvider.log(
                    trackId,
                    new InfoLog(
                        timestamp(),
                        String.format(
                            "Обработка команды %s завершилась с ошибкой, время выполнения составило %s мс",
                            command,
                            duration
                        )
                    )
                );
            }
        } catch (Exception exception) {
            loggerProvider.log(
                trackId,
                new ErrorLog(
                    timestamp(),
                    String.format("При обработке команды %s произошла ошибка: %s", command, exception.getMessage()),
                    exception
                )
            );
            throw exception;
        }
    }

    protected abstract Result businessProcess(C command);

    private <V> void validate(V value) {
        final Set<ConstraintViolation<V>> violations = validator.validate(value);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private LocalDateTime timestamp() {
        return dateTimeProvider.nowDateTime();
    }

    private Result execute(C command) {
        validate(command);
        return businessProcess(command);
    }
}
