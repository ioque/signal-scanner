package ru.ioque.investfund.adapters.telegrambot;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ioque.investfund.application.command.handlers.telegrambot.TelegramBotService;
import ru.ioque.investfund.domain.telegrambot.TelegramCommand;

@Slf4j
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SignalTelegramBot extends TelegramBot {
    TelegramBotService telegramBotService;

    public SignalTelegramBot(
        @Value("${telegram-bot.token}") String botToken,
        TelegramBotService telegramBotService
    ) {
        super(botToken);
        this.telegramBotService = telegramBotService;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            telegramBotService.execute(new TelegramCommand(
                update.getMessage().getChatId(),
                update.getMessage().getText()
            ));
        }
    }
}
