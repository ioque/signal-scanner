package ru.ioque.investfund.adapters.telegrambot;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ioque.investfund.application.modules.telegrambot.TelegramBotService;
import ru.ioque.investfund.domain.telegrambot.TelegramCommand;

@Slf4j
@Component
public class SignalTelegramBot extends TelegramBot {
    private final TelegramBotService telegramBotService;

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

    @PostConstruct
    @Profile("!tests")
    public void init() {
        new TelegramBotRegistrator().registerBot(this);
    }
}
