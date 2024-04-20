package ru.ioque.investfund.application.integration;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.telegrambot.TelegramBotService;
import ru.ioque.investfund.application.integration.event.SignalRegisteredEvent;

@Component
@AllArgsConstructor
public class SignalRegisteredEventHandler extends EventHandler<SignalRegisteredEvent> {
    TelegramBotService telegramBotService;

    @Override
    public void handle(SignalRegisteredEvent event) {
        telegramBotService.sendToAllChats(event.toString());
    }
}
