package ru.ioque.investfund.application.modules.risk.command;

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
import ru.ioque.investfund.application.modules.api.Command;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OpenEmulatedPosition implements Command {
    @NotNull(message = "Не указан идентификатор сканера данных.")
    @Valid
    ScannerId scannerId;
    @NotNull(message = "Не указан идентификатор инструмента.")
    @Valid
    InstrumentId instrumentId;
    @NotNull(message = "Не указана цена открытия позиции.")
    @DecimalMin(value = "0", inclusive = false, message = "Цена открытия должна быть больше нуля.")
    Double price;
}
