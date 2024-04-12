package ru.ioque.investfund.domain.scanner.command;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.Command;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class ScanningCommand implements Command {
    @NotNull(message = "Не передан идентификатор источника данных.")
    UUID datasourceId;
    @NotNull(message = "Не передан watermark.")
    LocalDateTime watermark;
}
