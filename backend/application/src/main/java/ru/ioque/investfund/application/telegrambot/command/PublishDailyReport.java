package ru.ioque.investfund.application.telegrambot.command;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.api.command.Command;

import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublishDailyReport extends Command {
    private Long chatId;

    @Builder
    public PublishDailyReport(UUID track, Long chatId) {
        super(track);
        this.chatId = chatId;
    }
}
