package ru.ioque.investfund.application.integration.handlers;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.CommandJournal;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.integration.EventHandler;
import ru.ioque.investfund.application.integration.event.TradingStateChanged;
import ru.ioque.investfund.application.modules.risk.command.EvaluateEmulatedPosition;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TradingStateChangedHandler extends EventHandler<TradingStateChanged> {
    CommandJournal commandJournal;

    public TradingStateChangedHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        CommandJournal commandJournal
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.commandJournal = commandJournal;
    }

    @Override
    protected void handle(TradingStateChanged event) {
        commandJournal.publish(new EvaluateEmulatedPosition(
            InstrumentId.from(event.getInstrumentId()),
            event.getPrice()
        ));
    }
}
