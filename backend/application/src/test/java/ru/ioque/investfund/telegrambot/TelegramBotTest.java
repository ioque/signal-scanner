package ru.ioque.investfund.telegrambot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.domain.telegrambot.TelegramCommand;
import ru.ioque.investfund.domain.telegrambot.TelegramMessage;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TELEGRAM BOT TEST")
public class TelegramBotTest extends BaseTest {
    @Test
    @DisplayName("""
        T1. Регистрация нового пользователя.
        """)
    void testCase1() {
        final TelegramCommand command = new TelegramCommand(1L, "/start");
        final LocalDateTime today = LocalDateTime.parse("2024-01-10T10:00:00");
        initTodayDateTime("2024-01-10T10:00:00");

        telegramBotService().execute(command);

        assertTrue(telegramChatRepository().findBy(command.getChatId()).isPresent());
        assertEquals(command.getChatId(), telegramChatRepository().findBy(command.getChatId()).get().getChatId());
        assertEquals(today, telegramChatRepository().findBy(command.getChatId()).get().getCreatedAt());
    }

    @Test
    @DisplayName("""
        T2. Подписка на обновление.
        """)
    void testCase2() {
        final TelegramCommand command = new TelegramCommand(1L, "/subscribe");
        final LocalDateTime today = LocalDateTime.parse("2024-01-10T10:00:00");
        initTodayDateTime("2024-01-10T10:00:00");

        telegramBotService().execute(command);

        assertTrue(telegramChatRepository().findBy(command.getChatId()).isPresent());
        assertEquals(command.getChatId(), telegramChatRepository().findBy(command.getChatId()).get().getChatId());
        assertEquals(today, telegramChatRepository().findBy(command.getChatId()).get().getCreatedAt());
    }

    @Test
    @DisplayName("""
        T3. Отписка от обновлений
        """)
    void testCase3() {
        telegramBotService().execute(new TelegramCommand(1L, "/start"));
        final TelegramCommand command = new TelegramCommand(1L, "/unsubscribe");

        telegramBotService().execute(command);

        assertTrue(telegramChatRepository().findBy(command.getChatId()).isEmpty());
        assertEquals(1, telegramMessageSender().getMessages().size());
        TelegramMessage telegramMessage = telegramMessageSender().getMessages().get(0);
        assertEquals(command.getChatId(), telegramMessage.getChatId());
        assertEquals("Вы успешно отписались от рассылки данных.", telegramMessage.getText());
    }

    @Test
    @DisplayName("""
        T4. Отписка от обновлений по несуществующему чату.
        """)
    void testCase4() {
        final TelegramCommand command = new TelegramCommand(1L, "/unsubscribe");

        final IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> telegramBotService().execute(command)
        );

        assertEquals(String.format("Чат[id=%s] не существует.", command.getChatId()), exception.getMessage());
    }

    @Test
    @DisplayName("""
        T5. Повторная подписка на обновление.
        """)
    void testCase5() {
        telegramBotService().execute(new TelegramCommand(1L, "/start"));
        telegramBotService().execute(new TelegramCommand(1L, "/start"));
        telegramBotService().execute(new TelegramCommand(1L, "/subscribe"));
        telegramBotService().execute(new TelegramCommand(1L, "/subscribe"));

        assertEquals(1, telegramChatRepository().findAll().size());
    }
}
