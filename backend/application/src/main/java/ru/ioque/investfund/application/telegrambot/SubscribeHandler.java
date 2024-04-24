package ru.ioque.investfund.application.telegrambot;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.TelegramChatRepository;
import ru.ioque.investfund.application.adapters.TelegramMessageSender;
import ru.ioque.investfund.application.api.command.CommandHandler;
import ru.ioque.investfund.application.telegrambot.command.Subscribe;
import ru.ioque.investfund.domain.telegrambot.TelegramChat;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SubscribeHandler extends CommandHandler<Subscribe> {
    TelegramChatRepository telegramChatRepository;
    TelegramMessageSender telegramMessageSender;

    public SubscribeHandler(
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
    protected void businessProcess(Subscribe command) {
        if(telegramChatRepository.findBy(command.getChatId()).isPresent()) {
            return;
        }
        telegramChatRepository.save(new TelegramChat(command.getChatId(), dateTimeProvider.nowDateTime()));
        telegramMessageSender.sendMessage(command.getChatId(), "Вы успешно подписались на получение торговых сигналов.");
    }
}
