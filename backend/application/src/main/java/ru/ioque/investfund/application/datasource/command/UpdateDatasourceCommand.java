package ru.ioque.investfund.application.datasource.command;

import jakarta.validation.constraints.NotBlank;
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
public class UpdateDatasourceCommand extends Command {
    @NotNull(message = "Не передан идентификатор источника данных.")
    DatasourceId id;
    @NotBlank(message = "Не передано название источника данных.")
    String name;
    @NotBlank(message = "Не передан адрес источника данных.")
    String url;
    @NotBlank(message = "Не передано описание источника данных.")
    String description;

    @Builder
    public UpdateDatasourceCommand(UUID track, DatasourceId id, String name, String url, String description) {
        super(track);
        this.id = id;
        this.name = name;
        this.url = url;
        this.description = description;
    }
}
