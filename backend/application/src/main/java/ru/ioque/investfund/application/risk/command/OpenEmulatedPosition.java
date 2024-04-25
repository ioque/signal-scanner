package ru.ioque.investfund.application.risk.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.api.command.Command;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OpenEmulatedPosition extends Command {
    @NotNull(message = "Не указан идентификатор сканера данных.")
    @Valid
    ScannerId scannerId;
    @NotNull(message = "Не указан идентификатор инструмента.")
    @Valid
    InstrumentId instrumentId;
    @NotNull(message = "Не указана цена открытия позиции.")
    @DecimalMin(value = "0", inclusive = false, message = "Цена открытия должна быть больше нуля.")
    Double price;

    @Builder
    public OpenEmulatedPosition(UUID track, ScannerId scannerId, InstrumentId instrumentId, Double price) {
        super(track);
        this.scannerId = scannerId;
        this.instrumentId = instrumentId;
        this.price = price;
    }
}
