package ru.ioque.investfund.application.adapters;

import java.io.File;

public interface TelegramMessageSender {
    void sendMessage(Long chatId, String message);
    void sendMessage(Long chatId, String message, File file);
}
