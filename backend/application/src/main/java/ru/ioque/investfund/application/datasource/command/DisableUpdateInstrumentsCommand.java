package ru.ioque.investfund.application.datasource.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DisableUpdateInstrumentsCommand extends Command {
    @NotNull(message = "Не передан идентификатор источника данных.")
    DatasourceId datasourceId;
    @NotEmpty(message = "Не передан список тикеров инструментов для активации обновления торговых данных.")
    List<@Valid Ticker> tickers;

    @Builder
    public DisableUpdateInstrumentsCommand(UUID track, DatasourceId datasourceId, List<Ticker> tickers) {
        super(track);
        this.datasourceId = datasourceId;
        this.tickers = tickers;
    }
}
