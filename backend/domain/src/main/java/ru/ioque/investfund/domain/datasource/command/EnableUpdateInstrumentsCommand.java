package ru.ioque.investfund.domain.datasource.command;

import jakarta.validation.constraints.NotBlank;
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
import ru.ioque.investfund.domain.core.Command;
import ru.ioque.investfund.domain.datasource.entity.indetity.DatasourceId;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnableUpdateInstrumentsCommand implements Command {
    @NotNull(message = "Не передан идентификатор источника данных.")
    DatasourceId datasourceId;
    @NotEmpty(message = "Не передан список тикеров для активации обновления.")
    List<@NotBlank(message = "Тикер не может быть пустым.") String> tickers;
}
