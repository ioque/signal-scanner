package ru.ioque.investfund.adapters.telegrambot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.ioque.investfund.adapters.persistence.entity.telegrambot.TelegramChatEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaTelegramChatRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.domain.scanner.event.SignalEvent;

@Slf4j
@Getter
public class SignalTelegramBot extends TelegramBot {
    private final TelegramClient telegramClient;
    private final DateTimeProvider dateTimeProvider;
    private final JpaTelegramChatRepository telegramChatRepository;

    public SignalTelegramBot(
        @Value("${telegram-bot.token}") String botToken,
        TelegramClient telegramClient,
        DateTimeProvider dateTimeProvider,
        JpaTelegramChatRepository telegramChatRepository
    ) {
        super(botToken);
        this.telegramClient = telegramClient;
        this.dateTimeProvider = dateTimeProvider;
        this.telegramChatRepository = telegramChatRepository;
    }

    @Override
    public void consume(Update update) {
        if(update.hasMessage()) {
            telegramChatRepository.save(new TelegramChatEntity(update.getMessage().getChatId(), dateTimeProvider.nowDateTime()));
        }
    }

    public void sentToAllChats(SignalEvent event){
        telegramChatRepository.findAll().forEach(chat -> {
            SendMessage sendMessage = SendMessage.builder()
                .chatId(String.valueOf(chat.getChatId()))
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
