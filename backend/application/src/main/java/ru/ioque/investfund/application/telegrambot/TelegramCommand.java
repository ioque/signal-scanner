package ru.ioque.investfund.application.telegrambot;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.application.api.command.Command;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class TelegramCommand implements Command {
    private final Long chatId;
    private final String message;
}
