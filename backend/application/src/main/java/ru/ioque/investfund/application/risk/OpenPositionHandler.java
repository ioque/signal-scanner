package ru.ioque.investfund.application.risk;

import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.api.command.CommandHandler;
import ru.ioque.investfund.application.risk.command.OpenPosition;

@Component
public class OpenPositionHandler extends CommandHandler<OpenPosition> {
    public OpenPositionHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider
    ) {
        super(dateTimeProvider, validator, loggerProvider);
    }

    @Override
    protected void businessProcess(OpenPosition command) {
        System.out.println(command);
    }
}
