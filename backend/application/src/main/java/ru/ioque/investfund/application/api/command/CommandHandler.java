package ru.ioque.investfund.application.api.command;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.domain.core.ApplicationLog;
import ru.ioque.investfund.domain.core.ErrorLog;
import ru.ioque.investfund.domain.core.InfoLog;

import java.time.LocalDateTime;
import java.util.List;
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
            execute(command).forEach(log -> loggerProvider.log(trackId, log));
            final long duration = System.currentTimeMillis() - start;
            loggerProvider.log(
                trackId,
                new InfoLog(
                    timestamp(),
                    String.format("Комада %s успешно обработана, время выполнения составило %s мс", command, duration)
                )
            );
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

    protected abstract List<ApplicationLog> businessProcess(C command);

    private <V> void validate(V value) {
        final Set<ConstraintViolation<V>> violations = validator.validate(value);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private LocalDateTime timestamp() {
        return dateTimeProvider.nowDateTime();
    }

    private List<ApplicationLog> execute(C command) {
        validate(command);
        return businessProcess(command);
    }
}
