package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.TelegramMessageSender;
import ru.ioque.investfund.domain.telegrambot.TelegramMessage;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FakeTelegramMessageSender implements TelegramMessageSender {
    private final List<TelegramMessage> messages = new CopyOnWriteArrayList<>();

    @Override
    public void sendMessage(TelegramMessage message) {
        messages.add(message);
    }

    public List<TelegramMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void clear() {
        messages.clear();
    }
}
