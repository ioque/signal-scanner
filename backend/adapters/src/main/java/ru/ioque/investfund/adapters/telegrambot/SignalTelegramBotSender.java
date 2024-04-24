package ru.ioque.investfund.adapters.telegrambot;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.ioque.investfund.application.adapters.TelegramMessageSender;

import java.io.File;

@Slf4j
@Component
@Profile("!tests")
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SignalTelegramBotSender implements TelegramMessageSender {
    TelegramClient telegramClient;

    @Override
    public void sendMessage(Long chatId, String message) {
        try {
            telegramClient.execute(new SendMessage(String.valueOf(chatId), message));
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(Long chatId, String message, File file) {
        try {
            telegramClient.execute(
                SendDocument.builder()
                    .chatId(String.valueOf(chatId))
                    .document(new InputFile(file))
                    .caption(message)
                    .build()
            );
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
