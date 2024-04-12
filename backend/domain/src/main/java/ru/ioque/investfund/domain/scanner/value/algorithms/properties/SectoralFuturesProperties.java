package ru.ioque.investfund.domain.scanner.value.algorithms.properties;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SectoralFuturesProperties {
    @DecimalMin(value = "0", inclusive = false, message = "Параметр futuresOvernightScale должен быть больше 0.")
    Double futuresOvernightScale;
    @DecimalMin(value = "0", inclusive = false, message = "Параметр stockOvernightScale должен быть больше 0.")
    Double stockOvernightScale;
    @NotBlank(message = "Не передан тикер фьючерса.")
    String futuresTicker;
}
