package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.TelegramMessageSender;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FakeTelegramMessageSender implements TelegramMessageSender {
    public Map<Long, List<String>> messages = new ConcurrentHashMap<>();

    @Override
    public void sendMessage(Long chatId, String message) {
        messages.put(chatId, List.of(message));
    }

    @Override
    public void sendMessage(Long chatId, String message, File fileSystemResource) {
        messages.put(chatId, List.of(message));
    }

    public void clear() {
        messages.clear();
    }
}
