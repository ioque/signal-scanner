package ru.ioque.investfund.adapters.telegrambot;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class TelegramBot implements LongPollingSingleThreadUpdateConsumer {
    String botToken;

    @Override
    public abstract void consume(Update update);
}
