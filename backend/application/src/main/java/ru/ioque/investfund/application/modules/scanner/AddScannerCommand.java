package ru.ioque.investfund.application.modules.scanner;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.SignalConfig;

@Getter
@ToString
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AddScannerCommand {
    String description;
    SignalConfig signalConfig;

    @Builder
    public AddScannerCommand(
        String description,
        SignalConfig signalConfig
    ) {
        this.description = description;
        this.signalConfig = signalConfig;
    }
}
