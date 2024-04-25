package ru.ioque.investfund.application.api.event.handlers;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.CommandPublisher;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.api.event.EventHandler;
import ru.ioque.investfund.application.datasource.event.TradingStateChanged;
import ru.ioque.investfund.application.risk.command.EvaluateEmulatedPosition;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TradingStateChangedHandler extends EventHandler<TradingStateChanged> {
    CommandPublisher commandPublisher;

    public TradingStateChangedHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        UUIDProvider uuidProvider,
        CommandPublisher commandPublisher
    ) {
        super(dateTimeProvider, validator, loggerProvider, uuidProvider);
        this.commandPublisher = commandPublisher;
    }

    @Override
    protected void handle(TradingStateChanged event) {
        commandPublisher.publish(new EvaluateEmulatedPosition(
            uuidProvider.generate(),
            InstrumentId.from(event.getInstrumentId()),
            event.getPrice()
        ));
    }
}
