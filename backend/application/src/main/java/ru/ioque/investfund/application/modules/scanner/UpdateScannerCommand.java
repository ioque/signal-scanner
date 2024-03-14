package ru.ioque.investfund.application.modules.scanner;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UpdateScannerCommand {
    UUID id;
    String description;
    List<UUID> ids;

    @Builder
    public UpdateScannerCommand(
        UUID id,
        String description,
        List<UUID> ids
    ) {
        this.id = id;
        this.ids = ids;
        this.description = description;
    }
}
