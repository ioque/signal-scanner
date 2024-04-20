package ru.ioque.investfund.application.integration;

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
public abstract class EventHandler<E> {
    protected DateTimeProvider dateTimeProvider;
    protected Validator validator;
    protected LoggerProvider loggerProvider;

    public void handleFor(E event) {
        loggerProvider.log(new InfoLog(
            dateTimeProvider.nowDateTime(),
            String.format("Получено событие %s", event)
        ));
        validateEvent(event);
        try {
            handle(event);
            loggerProvider.log(
                new InfoLog(
                    dateTimeProvider.nowDateTime(),
                    String.format("Событие %s успешно обработано.", event)
                )
            );
        } catch (Exception e) {
            loggerProvider.log(
                new ErrorLog(
                    dateTimeProvider.nowDateTime(),
                    String.format("При обработке события %s произошла ошибка: %s", event, e.getMessage()), e)
            );
            throw e;
        }
    }

    protected abstract void handle(E event);

    private void validateEvent(E event) {
        final Set<ConstraintViolation<E>> violations = validator.validate(event);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
