package ru.ioque.investfund.application.integration;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.integration.event.SignalRegistered;
import ru.ioque.investfund.application.telegrambot.TelegramBotService;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SignalRegisteredEventHandler extends EventHandler<SignalRegistered> {
    TelegramBotService telegramBotService;

    public SignalRegisteredEventHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        TelegramBotService telegramBotService
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.telegramBotService = telegramBotService;
    }

    @Override
    public void handle(SignalRegistered event) {
        telegramBotService.sendToAllChats(event.toString());
    }
}
