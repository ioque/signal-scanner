package ru.ioque.investfund.application.modules.telegrambot.handler;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.repository.TelegramChatRepository;
import ru.ioque.investfund.application.adapters.TelegramMessageSender;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.telegrambot.command.Unsubscribe;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UnsubscribeHandler extends CommandHandler<Unsubscribe> {
    TelegramChatRepository telegramChatRepository;
    TelegramMessageSender telegramMessageSender;

    public UnsubscribeHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        TelegramChatRepository telegramChatRepository,
        TelegramMessageSender telegramMessageSender
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.telegramChatRepository = telegramChatRepository;
        this.telegramMessageSender = telegramMessageSender;
    }

    @Override
    protected Result businessProcess(Unsubscribe command) {
        if (telegramChatRepository.findBy(command.getChatId()).isEmpty()) {
            throw new IllegalArgumentException(String.format("Чат[id=%s] не существует.", command.getChatId()));
        }
        telegramChatRepository.removeBy(command.getChatId());
        telegramMessageSender.sendMessage(command.getChatId(), "Вы успешно отписались от получения торговых сигналов.");
        return Result.success();
    }
}
