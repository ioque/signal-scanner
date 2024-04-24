package ru.ioque.investfund.application.risk;

import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.api.command.CommandHandler;
import ru.ioque.investfund.application.risk.command.OpenEmulatedPosition;

@Component
public class OpenEmulatedPositionHandler extends CommandHandler<OpenEmulatedPosition> {
    public OpenEmulatedPositionHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider
    ) {
        super(dateTimeProvider, validator, loggerProvider);
    }

    @Override
    protected void businessProcess(OpenEmulatedPosition command) {
        System.out.println(command);
    }
}
