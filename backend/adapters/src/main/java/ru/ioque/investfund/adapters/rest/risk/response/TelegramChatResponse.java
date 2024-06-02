package ru.ioque.investfund.adapters.rest.risk.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.psql.entity.telegrambot.TelegramChatEntity;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TelegramChatResponse {
    Long chatId;
    String name;
    LocalDateTime createdAt;

    public static TelegramChatResponse from(TelegramChatEntity telegramChatEntity) {
        return TelegramChatResponse.builder()
            .chatId(telegramChatEntity.getChatId())
            .name(telegramChatEntity.getName())
            .createdAt(telegramChatEntity.getCreatedAt())
            .build();
    }
}
