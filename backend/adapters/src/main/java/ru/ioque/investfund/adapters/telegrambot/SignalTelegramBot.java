package ru.ioque.investfund.adapters.telegrambot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.ioque.investfund.domain.scanner.event.SignalEvent;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Getter
public class SignalTelegramBot extends TelegramBot {
    private final Set<Long> chatIds = new HashSet<>();
    private final TelegramClient telegramClient;

    public SignalTelegramBot(String botToken) {
        super(botToken);
        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        if(update.hasMessage()) {
            chatIds.add(update.getMessage().getChatId());
        }
    }

    public void sentToAllChats(SignalEvent event){
        chatIds.forEach(chatId -> {
            SendMessage sendMessage = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(event.toString())
                .build();
            try {
                telegramClient.execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}
