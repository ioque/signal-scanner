package ru.ioque.investfund.application.telegrambot;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.TelegramChatRepository;
import ru.ioque.investfund.application.adapters.TelegramMessageSender;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.api.command.CommandHandler;
import ru.ioque.investfund.application.telegrambot.command.Unsubscribe;
import ru.ioque.investfund.domain.core.InfoLog;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UnsubscribeHandler extends CommandHandler<Unsubscribe> {
    TelegramChatRepository telegramChatRepository;
    TelegramMessageSender telegramMessageSender;

    public UnsubscribeHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        UUIDProvider uuidProvider,
        TelegramChatRepository telegramChatRepository,
        TelegramMessageSender telegramMessageSender
    ) {
        super(dateTimeProvider, validator, loggerProvider, uuidProvider);
        this.telegramChatRepository = telegramChatRepository;
        this.telegramMessageSender = telegramMessageSender;
    }

    @Override
    protected void businessProcess(Unsubscribe command) {
        if (telegramChatRepository.findBy(command.getChatId()).isEmpty()) {
            throw new IllegalArgumentException(String.format("Чат[id=%s] не существует.", command.getChatId()));
        }
        telegramChatRepository.removeBy(command.getChatId());
        telegramMessageSender.sendMessage(command.getChatId(), "Вы успешно отписались от получения торговых сигналов.");
        loggerProvider.log(new InfoLog(
            dateTimeProvider.nowDateTime(),
            String.format("Пользователь[chatId=%s] отписался от получения торговых сигналов", command.getChatId()),
            command.getTrack()
        ));
    }
}
