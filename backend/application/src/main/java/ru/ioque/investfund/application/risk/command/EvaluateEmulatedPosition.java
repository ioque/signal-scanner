package ru.ioque.investfund.application.risk.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.api.command.Command;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EvaluateEmulatedPosition implements Command {
    @NotNull(message = "Не указан идентификатор инструмента.")
    @Valid
    InstrumentId instrumentId;
    @NotNull(message = "Не указана последняя цена инструмента.")
    @DecimalMin(value = "0", inclusive = false, message = "Последняя цена инструмента должна быть больше нуля.")
    Double price;
}
