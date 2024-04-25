package ru.ioque.investfund.application.datasource.command;

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

import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntegrateInstrumentsCommand extends Command {
    @NotNull(message = "Не передан идентификатор источника данных.")
    DatasourceId datasourceId;

    @Builder
    public IntegrateInstrumentsCommand(UUID track, DatasourceId datasourceId) {
        super(track);
        this.datasourceId = datasourceId;
    }
}
