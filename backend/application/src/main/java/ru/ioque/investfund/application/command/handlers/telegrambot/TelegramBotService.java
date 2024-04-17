package ru.ioque.investfund.application.command.handlers.telegrambot;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.TelegramChatRepository;
import ru.ioque.investfund.application.adapters.TelegramMessageSender;
import ru.ioque.investfund.domain.telegrambot.TelegramChat;
import ru.ioque.investfund.domain.telegrambot.TelegramCommand;
import ru.ioque.investfund.domain.telegrambot.TelegramMessage;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TelegramBotService {
    DateTimeProvider dateTimeProvider;
    TelegramChatRepository telegramChatRepository;
    TelegramMessageSender telegramMessageSender;

    public void execute(TelegramCommand command) {
        switch (command.getMessage()) {
            case "/start":
            case "/subscribe":
                registerChat(command.getChatId());
                break;
            case "/unsubscribe":
                unregisterChat(command.getChatId());
                break;
            default:
                log.warn("Неподдерживаемая команда: {}", command.getMessage());
        }
    }

    public void sendToAllChats(String message) {
        telegramChatRepository
            .findAll()
            .forEach(chat -> telegramMessageSender.sendMessage(new TelegramMessage(chat.getChatId(), message)));
    }

    public void registerChat(long chatId) {
        if(telegramChatRepository.findBy(chatId).isPresent()) {
            return;
        }
        telegramChatRepository.save(new TelegramChat(chatId, dateTimeProvider.nowDateTime()));
    }

    public void unregisterChat(long chatId) {
        if (telegramChatRepository.findBy(chatId).isEmpty()) {
            throw new IllegalArgumentException(String.format("Чат[id=%s] не существует.", chatId));
        }
        telegramChatRepository.removeBy(chatId);
        telegramMessageSender.sendMessage(new TelegramMessage(chatId, "Вы успешно отписались от рассылки данных."));
    }
}
