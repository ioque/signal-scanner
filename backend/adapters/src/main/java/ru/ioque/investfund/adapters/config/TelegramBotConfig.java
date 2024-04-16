package ru.ioque.investfund.adapters.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.ioque.investfund.adapters.telegrambot.SignalTelegramBot;
import ru.ioque.investfund.adapters.telegrambot.TelegramBotRegistrator;

@Configuration
@Profile("!tests")
public class TelegramBotConfig {
    @Value("${telegram-bot.token}")
    private String botToken;

    @Bean
    public SignalTelegramBot telegramBot() {
        TelegramBotRegistrator registrator = new TelegramBotRegistrator();
        SignalTelegramBot signalTelegramBot = new SignalTelegramBot(botToken);
        registrator.registerBot(signalTelegramBot);
        return signalTelegramBot;
    }
}
