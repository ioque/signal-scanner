package ru.ioque.investfund.application.event.handlers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.command.handlers.telegrambot.TelegramBotService;
import ru.ioque.investfund.application.event.EventHandler;
import ru.ioque.investfund.domain.scanner.event.SignalFoundEvent;

@Component
@AllArgsConstructor
public class SignalFoundHandler extends EventHandler<SignalFoundEvent> {
    TelegramBotService telegramBotService;

    @Override
    public void handle(SignalFoundEvent event) {
        telegramBotService.sendToAllChats(event.toString());
    }
}
