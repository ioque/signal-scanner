package ru.ioque.investfund.domain.datasource.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.Command;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DisableUpdateInstrumentsCommand implements Command {
    @NotNull(message = "Не передан идентификатор источника данных.")
    UUID datasourceId;
    @NotEmpty(message = "Не передан список тикеров для активации обновления.")
    List<@NotBlank(message = "Тикер не может быть пустым.") String> tickers;
}
