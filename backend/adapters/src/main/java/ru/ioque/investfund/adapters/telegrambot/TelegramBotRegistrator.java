package ru.ioque.investfund.adapters.telegrambot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

@Slf4j
public class TelegramBotRegistrator {
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
