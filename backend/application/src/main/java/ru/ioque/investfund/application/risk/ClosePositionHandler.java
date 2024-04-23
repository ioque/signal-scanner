package ru.ioque.investfund.application.risk;

import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.api.command.CommandHandler;
import ru.ioque.investfund.application.risk.command.ClosePosition;

@Component
public class ClosePositionHandler extends CommandHandler<ClosePosition> {
    public ClosePositionHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider
    ) {
        super(dateTimeProvider, validator, loggerProvider);
    }

    @Override
    protected void businessProcess(ClosePosition command) {
        System.out.println(command);
    }
}
