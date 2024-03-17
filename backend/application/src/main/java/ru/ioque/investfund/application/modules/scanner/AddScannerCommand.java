package ru.ioque.investfund.application.modules.scanner;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.configurator.ScannerConfiguration;

@Getter
@ToString
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AddScannerCommand {
    ScannerConfiguration scannerConfiguration;

    @Builder
    public AddScannerCommand(
        ScannerConfiguration scannerConfiguration
    ) {
        this.scannerConfiguration = scannerConfiguration;
    }
}
