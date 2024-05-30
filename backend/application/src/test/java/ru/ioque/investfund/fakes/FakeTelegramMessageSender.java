package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.TelegramMessageSender;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FakeTelegramMessageSender implements TelegramMessageSender {
    public Map<Long, List<String>> messages = new ConcurrentHashMap<>();

    @Override
    public void sendMessage(Long chatId, String message) {
        if (!messages.containsKey(chatId)) {
            messages.put(chatId, new ArrayList<>());
        }
        messages.get(chatId).add(message);
    }

    @Override
    public void sendMessage(Long chatId, String message, File fileSystemResource) {
        sendMessage(chatId, message);
    }

    public void clear() {
        messages.clear();
    }
}
