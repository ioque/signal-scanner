package ru.ioque.investfund.application.modules.scanner;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.configurator.ScannerConfiguration;

import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UpdateScannerCommand {
    UUID id;
    ScannerConfiguration scannerConfiguration;

    @Builder
    public UpdateScannerCommand(
        UUID id,
        ScannerConfiguration scannerConfiguration
    ) {
        this.id = id;
        this.scannerConfiguration = scannerConfiguration;
    }
}
