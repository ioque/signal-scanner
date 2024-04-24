package ru.ioque.investfund.adapters.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.TelegramMessageSender;

import java.io.File;

@Slf4j
@Component
public class FakeTelegramSender implements TelegramMessageSender {
    @Override
    public void sendMessage(Long chatId, String message) {
        log.info("Sending message: {}", message);
    }

    @Override
    public void sendMessage(Long chatId, String message, File fileSystemResource) {
        log.info("Sending document: {}", fileSystemResource);
    }
}
