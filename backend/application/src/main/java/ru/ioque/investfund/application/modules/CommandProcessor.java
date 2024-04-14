package ru.ioque.investfund.application.modules;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import ru.ioque.investfund.application.share.logger.LoggerFacade;

import java.util.Set;

@AllArgsConstructor
public abstract class CommandProcessor<C> {
    protected Validator validator;
    protected LoggerFacade loggerFacade;

    public void handle(C command) {
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
}
