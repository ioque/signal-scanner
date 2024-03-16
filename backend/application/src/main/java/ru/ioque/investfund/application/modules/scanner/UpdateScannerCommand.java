package ru.ioque.investfund.application.modules.scanner;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.SignalConfig;

import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UpdateScannerCommand {
    UUID id;
    SignalConfig signalConfig;

    @Builder
    public UpdateScannerCommand(
        UUID id,
        SignalConfig signalConfig
    ) {
        this.id = id;
        this.signalConfig = signalConfig;
    }
}
