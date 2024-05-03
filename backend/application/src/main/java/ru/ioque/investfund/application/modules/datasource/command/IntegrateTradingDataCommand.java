package ru.ioque.investfund.application.modules.datasource.command;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.modules.api.Command;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntegrateTradingDataCommand implements Command {
    @NotNull(message = "Не передан идентификатор источника данных.")
    DatasourceId datasourceId;
}
