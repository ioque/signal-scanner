package ru.ioque.investfund.domain.telegrambot;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class TelegramMessage {
    private final Long chatId;
    private final String text;
}