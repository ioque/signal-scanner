package ru.ioque.investfund.application.telegrambot.command;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.api.command.Command;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublishSignal extends Command {
    Boolean isBuy;
    ScannerId scannerId;
    InstrumentId instrumentId;

    @Builder
    public PublishSignal(UUID track, Boolean isBuy, ScannerId scannerId, InstrumentId instrumentId) {
        super(track);
        this.isBuy = isBuy;
        this.scannerId = scannerId;
        this.instrumentId = instrumentId;
    }
}
