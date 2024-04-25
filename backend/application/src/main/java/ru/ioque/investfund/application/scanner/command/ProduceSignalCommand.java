package ru.ioque.investfund.application.scanner.command;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.api.command.Command;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProduceSignalCommand extends Command {
    @NotNull(message = "Не передан идентификатор источника данных.")
    DatasourceId datasourceId;
    @NotNull(message = "Не передан watermark.")
    LocalDateTime watermark;

    @Builder
    public ProduceSignalCommand(UUID track, DatasourceId datasourceId, LocalDateTime watermark) {
        super(track);
        this.datasourceId = datasourceId;
        this.watermark = watermark;
    }
}
