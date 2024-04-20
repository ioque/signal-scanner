package ru.ioque.investfund.domain.scanner.algorithms.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.AlgorithmType;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SectoralFuturesProperties implements AlgorithmProperties {
    AlgorithmType type = AlgorithmType.SECTORAL_FUTURES;
    @NotNull(message = "Не передан параметр futuresOvernightScale.")
    @DecimalMin(value = "0", inclusive = false, message = "Параметр futuresOvernightScale должен быть больше 0.")
    Double futuresOvernightScale;
    @NotNull(message = "Не передан параметр stockOvernightScale.")
    @DecimalMin(value = "0", inclusive = false, message = "Параметр stockOvernightScale должен быть больше 0.")
    Double stockOvernightScale;
    @NotNull(message = "Не передан идентификатор фьючерса на основной товар сектора.")
    @Valid
    Ticker futuresTicker;
}
