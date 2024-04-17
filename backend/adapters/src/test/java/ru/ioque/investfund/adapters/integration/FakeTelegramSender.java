package ru.ioque.investfund.adapters.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.TelegramMessageSender;
import ru.ioque.investfund.domain.telegrambot.TelegramMessage;

@Slf4j
@Component
public class FakeTelegramSender implements TelegramMessageSender {
    @Override
    public void sendMessage(TelegramMessage message) {
        log.info("Sending message: {}", message);
    }
}
