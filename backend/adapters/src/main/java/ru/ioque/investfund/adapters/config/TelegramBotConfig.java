package ru.ioque.investfund.adapters.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.ioque.investfund.adapters.telegrambot.TelegramBot;

import java.util.List;

@Slf4j
@Configuration
@Profile("!tests")
public class TelegramBotConfig {
    @Value("${telegram-bot.token}")
    private String botToken;

    @Bean
    public TelegramClient telegramClient() {
        return new OkHttpTelegramClient(botToken);
    }

    public TelegramBotConfig(
        @Value("${telegram-bot.token}") String botToken,
        @Autowired List<TelegramBot> telegramBots
    ) {
        this.botToken = botToken;
        telegramBots.forEach(this::registerBot);
    }

    public void registerBot(TelegramBot bot) {
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(bot.getBotToken(), bot);
            log.info("Telegram bot is ready.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
