package ru.ioque.investfund.adapters.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import ru.ioque.investfund.adapters.persistence.repositories.JpaTelegramChatRepository;
import ru.ioque.investfund.adapters.telegrambot.SignalTelegramBot;
import ru.ioque.investfund.adapters.telegrambot.TelegramBotRegistrator;
import ru.ioque.investfund.application.adapters.DateTimeProvider;

@Configuration
@Profile("!tests")
@RequiredArgsConstructor
public class TelegramBotConfig {
    @Value("${telegram-bot.token}")
    private String botToken;
    private final DateTimeProvider dateTimeProvider;
    private final JpaTelegramChatRepository jpaTelegramChatRepository;

    @Bean
    public SignalTelegramBot signalTelegramBot() {
        SignalTelegramBot signalTelegramBot = new SignalTelegramBot(
            botToken,
            new OkHttpTelegramClient(botToken),
            dateTimeProvider,
            jpaTelegramChatRepository
        );
        new TelegramBotRegistrator().registerBot(signalTelegramBot);
        return signalTelegramBot;
    }
}
