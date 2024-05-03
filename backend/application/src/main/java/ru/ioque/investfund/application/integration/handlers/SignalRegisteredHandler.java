package ru.ioque.investfund.application.integration.handlers;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.CommandPublisher;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.integration.EventHandler;
import ru.ioque.investfund.application.modules.risk.command.CloseEmulatedPosition;
import ru.ioque.investfund.application.modules.risk.command.OpenEmulatedPosition;
import ru.ioque.investfund.application.integration.event.SignalRegistered;
import ru.ioque.investfund.application.modules.telegrambot.command.PublishSignal;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SignalRegisteredHandler extends EventHandler<SignalRegistered> {
    CommandPublisher commandPublisher;

    public SignalRegisteredHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        CommandPublisher commandPublisher
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.commandPublisher = commandPublisher;
    }

    @Override
    public void handle(SignalRegistered event) {
        commandPublisher.publish(
            PublishSignal.builder()
                .isBuy(event.getIsBuy())
                .scannerId(ScannerId.from(event.getScannerId()))
                .instrumentId(InstrumentId.from(event.getInstrumentId()))
                .build()
        );
        if (event.getIsBuy()) {
            commandPublisher.publish(
                OpenEmulatedPosition.builder()
                    .price(event.getPrice())
                    .scannerId(ScannerId.from(event.getScannerId()))
                    .instrumentId(InstrumentId.from(event.getInstrumentId()))
                    .build()
            );
        } else {
            commandPublisher.publish(
                CloseEmulatedPosition.builder()
                    .price(event.getPrice())
                    .scannerId(ScannerId.from(event.getScannerId()))
                    .instrumentId(InstrumentId.from(event.getInstrumentId()))
                    .build()
            );
        }
    }
}