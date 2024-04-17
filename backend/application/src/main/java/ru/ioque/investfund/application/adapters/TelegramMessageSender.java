package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.telegrambot.TelegramMessage;

public interface TelegramMessageSender {
    void sendMessage(TelegramMessage message);
}
