package ru.ioque.investfund.adapters.integration.telegrambot;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.ioque.investfund.adapters.integration.InfrastructureTest;
import ru.ioque.investfund.adapters.persistence.repositories.JpaTelegramChatRepository;
import ru.ioque.investfund.adapters.telegrambot.SignalTelegramBot;
import ru.ioque.investfund.application.adapters.DateTimeProvider;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("TELEGRAM BOT TEST")
public class TelegramBotTest extends InfrastructureTest {
    private static final String BOT_TOKEN = "token";
    private final JpaTelegramChatRepository telegramChatRepository;
    private final DateTimeProvider dateTimeProvider;
    private final SignalTelegramBot signalTelegramBot;
    private final TelegramClient telegramClient;

    public TelegramBotTest(
        @Autowired JpaTelegramChatRepository telegramChatRepository,
        @Autowired DateTimeProvider dateTimeProvider
    ) {
        this.telegramChatRepository = telegramChatRepository;
        this.dateTimeProvider = dateTimeProvider;
        this.telegramClient = mock(TelegramClient.class);
        this.signalTelegramBot = new SignalTelegramBot(BOT_TOKEN, telegramClient, dateTimeProvider, telegramChatRepository);
    }

    @BeforeEach
    void beforeEach() {
        telegramChatRepository.deleteAll();
    }

    @Test
    @DisplayName("""
        T1. Регистрация нового пользователя.
        """)
    void testCase1() {
        long chatId = 1L;
        final Update update = prepareUpdateObject(chatId, "/start");
        final LocalDateTime today = LocalDateTime.parse("2024-01-10T10:00:00");
        dateTimeProvider.initToday(today);

        signalTelegramBot.consume(update);

        assertTrue(telegramChatRepository.findById(chatId).isPresent());
        assertEquals(chatId, telegramChatRepository.findById(chatId).get().getChatId());
        assertEquals(today, telegramChatRepository.findById(chatId).get().getCreatedAt());
    }

    private static @NotNull Update prepareUpdateObject(Long chatId, String text) {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);
        when(message.getText()).thenReturn(text);
        return update;
    }
}
