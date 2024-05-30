package ru.ioque.investfund.telegrambot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.telegrambot.command.Subscribe;
import ru.ioque.investfund.application.modules.telegrambot.command.Unsubscribe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UNSUBSCRIBE TELEGRAM BOT TEST")
public class UnsubscribeTelegramBotTest extends BaseTest {
    @Test
    @DisplayName("""
        T1. Отписка от обновлений
        """)
    void testCase1() {
        commandBus().execute(new Subscribe(1L, "kukusuku"));
        telegramMessageSender().clear();
        final Unsubscribe command = new Unsubscribe(1L);

        commandBus().execute(command);

        assertTrue(telegramChatRepository().findBy(command.getChatId()).isEmpty());
        assertEquals(
            "Вы успешно отписались от получения торговых сигналов.",
            telegramMessageSender().messages.get(command.getChatId()).get(0)
        );
    }

    @Test
    @DisplayName("""
        T2. Отписка от обновлений по несуществующему чату.
        """)
    void testCase2() {
        final IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> commandBus().execute(new Unsubscribe(1L))
        );

        assertEquals(String.format("Чат[id=%s] не существует.", 1L), exception.getMessage());
    }
}
