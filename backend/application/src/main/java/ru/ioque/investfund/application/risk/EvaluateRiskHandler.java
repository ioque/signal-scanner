package ru.ioque.investfund.application.risk;

import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.api.command.CommandHandler;
import ru.ioque.investfund.application.risk.command.EvaluateRisk;

@Component
public class EvaluateRiskHandler extends CommandHandler<EvaluateRisk> {
    public EvaluateRiskHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider
    ) {
        super(dateTimeProvider, validator, loggerProvider);
    }

    @Override
    protected void businessProcess(EvaluateRisk command) {
        System.out.println(command);
    }
}
