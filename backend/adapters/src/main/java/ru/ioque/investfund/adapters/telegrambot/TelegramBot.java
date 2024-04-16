package ru.ioque.investfund.adapters.telegrambot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@AllArgsConstructor
public abstract class TelegramBot implements LongPollingSingleThreadUpdateConsumer {
    private final String botToken;

    @Override
    public abstract void consume(Update update);
}
