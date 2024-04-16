package ru.ioque.investfund.adapters.telegrambot;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.ioque.investfund.domain.scanner.event.SignalEvent;

import java.util.HashSet;
import java.util.Set;

@Getter
@Component
@Profile("!tests")
public class Bot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final String botToken;
    private final Set<Long> chatIds = new HashSet<>();
    private final TelegramClient telegramClient;

    public Bot(@Value("${bot.token}") String botToken) {
        this.botToken = botToken;
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
                e.printStackTrace();
            }
        });
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }
}
