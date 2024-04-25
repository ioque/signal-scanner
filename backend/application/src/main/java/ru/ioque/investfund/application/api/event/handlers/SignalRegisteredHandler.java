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
import ru.ioque.investfund.application.risk.command.CloseEmulatedPosition;
import ru.ioque.investfund.application.risk.command.OpenEmulatedPosition;
import ru.ioque.investfund.application.scanner.event.SignalRegistered;
import ru.ioque.investfund.application.telegrambot.command.PublishSignal;
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
        UUIDProvider uuidProvider,
        CommandPublisher commandPublisher
    ) {
        super(dateTimeProvider, validator, loggerProvider, uuidProvider);
        this.commandPublisher = commandPublisher;
    }

    @Override
    public void handle(SignalRegistered event) {
        commandPublisher.publish(
            PublishSignal.builder()
                .track(uuidProvider.generate())
                .isBuy(event.getIsBuy())
                .scannerId(ScannerId.from(event.getScannerId()))
                .instrumentId(InstrumentId.from(event.getInstrumentId()))
                .build()
        );
        if (event.getIsBuy()) {
            commandPublisher.publish(
                OpenEmulatedPosition.builder()
                    .track(uuidProvider.generate())
                    .price(event.getPrice())
                    .scannerId(ScannerId.from(event.getScannerId()))
                    .instrumentId(InstrumentId.from(event.getInstrumentId()))
                    .build()
            );
        } else {
            commandPublisher.publish(
                CloseEmulatedPosition.builder()
                    .track(uuidProvider.generate())
                    .price(event.getPrice())
                    .scannerId(ScannerId.from(event.getScannerId()))
                    .instrumentId(InstrumentId.from(event.getInstrumentId()))
                    .build()
            );
        }
    }
}
