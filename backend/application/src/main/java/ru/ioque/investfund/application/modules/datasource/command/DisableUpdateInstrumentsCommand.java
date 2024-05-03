package ru.ioque.investfund.application.modules.datasource.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.util.List;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DisableUpdateInstrumentsCommand implements Command {
    @NotNull(message = "Не передан идентификатор источника данных.")
    DatasourceId datasourceId;
    @NotEmpty(message = "Не передан список тикеров инструментов для активации обновления торговых данных.")
    List<@Valid Ticker> tickers;
}
