package ru.ioque.investfund.application.modules.telegrambot.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.modules.api.Command;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.Signal;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublishSignal implements Command {
    Signal signal;
}
