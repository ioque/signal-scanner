package ru.ioque.investfund.domain.telegrambot;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.Command;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class TelegramCommand implements Command {
    private final Long chatId;
    private final String message;
}
