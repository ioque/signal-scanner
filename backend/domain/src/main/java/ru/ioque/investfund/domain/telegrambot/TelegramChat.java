package ru.ioque.investfund.domain.telegrambot;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class TelegramChat {
    private final Long chatId;
    private final LocalDateTime createdAt;
}
