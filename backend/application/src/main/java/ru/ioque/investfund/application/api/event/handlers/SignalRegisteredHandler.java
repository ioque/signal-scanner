package ru.ioque.investfund.application.api.event.handlers;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.CommandPublisher;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.api.event.EventHandler;
import ru.ioque.investfund.application.risk.command.ClosePosition;
import ru.ioque.investfund.application.risk.command.OpenPosition;
import ru.ioque.investfund.application.scanner.event.SignalRegistered;
import ru.ioque.investfund.application.telegrambot.TelegramBotService;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SignalRegisteredHandler extends EventHandler<SignalRegistered> {
    CommandPublisher commandPublisher;
    TelegramBotService telegramBotService;

    public SignalRegisteredHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        CommandPublisher commandPublisher,
        TelegramBotService telegramBotService
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.telegramBotService = telegramBotService;
        this.commandPublisher = commandPublisher;
    }

    @Override
    public void handle(SignalRegistered event) {
        telegramBotService.sendToAllChats(event.toString());
        if (event.getIsBuy()) {
            commandPublisher.publish(
                OpenPosition.builder()
                    .price(event.getPrice())
                    .scannerId(ScannerId.from(event.getScannerId()))
                    .instrumentId(InstrumentId.from(event.getInstrumentId()))
                    .build()
            );
        } else {
            commandPublisher.publish(
                ClosePosition.builder()
                    .scannerId(ScannerId.from(event.getScannerId()))
                    .instrumentId(InstrumentId.from(event.getInstrumentId()))
                    .build()
            );
        }
    }
}
