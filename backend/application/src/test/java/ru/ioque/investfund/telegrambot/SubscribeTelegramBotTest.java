package ru.ioque.investfund.telegrambot;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.telegrambot.command.Subscribe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SUBSCRIBE TELEGRAM BOT TEST")
public class SubscribeTelegramBotTest extends BaseTest {
    @Test
    @DisplayName("""
        T1. Подписка на обновление.
        """)
    void testCase1() {
        final Subscribe command = new Subscribe(1L, "kukusuku");
        final LocalDateTime today = LocalDateTime.parse("2024-01-10T10:00:00");
        initTodayDateTime("2024-01-10T10:00:00");

        commandBus().execute(command);

        assertTrue(telegramChatRepository().findBy(command.getChatId()).isPresent());
        assertEquals(command.getChatId(), telegramChatRepository().findBy(command.getChatId()).get().getChatId());
        assertEquals(today, telegramChatRepository().findBy(command.getChatId()).get().getCreatedAt());
        assertEquals(
            "Вы успешно подписались на получение торговых сигналов.",
            telegramMessageSender().messages.get(command.getChatId()).get(0)
        );
    }

    @Test
    @DisplayName("""
        T2. Повторная подписка на обновление.
        """)
    void testCase2() {
        commandBus().execute(new Subscribe(1L, "kukusuku"));
        commandBus().execute(new Subscribe(1L, "kukusuku"));

        assertEquals(1, telegramChatRepository().findAll().size());
    }
}
