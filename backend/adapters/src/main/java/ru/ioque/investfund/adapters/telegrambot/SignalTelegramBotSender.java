package ru.ioque.investfund.adapters.telegrambot;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.ioque.investfund.application.adapters.TelegramMessageSender;
import ru.ioque.investfund.domain.telegrambot.TelegramMessage;

@Slf4j
@Component
@Profile("!tests")
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SignalTelegramBotSender implements TelegramMessageSender {
    TelegramClient telegramClient;

    @Override
    public void sendMessage(TelegramMessage message) {
        try {
            telegramClient.execute(new SendMessage(String.valueOf(message.getChatId()), message.getText()));
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
