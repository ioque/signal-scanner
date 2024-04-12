package ru.ioque.investfund.domain.scanner.value.algorithms.properties;

import jakarta.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SectoralRetardProperties {
    @DecimalMin(value = "0", inclusive = false, message = "Параметр historyScale должен быть больше 0.")
    Double historyScale;
    @DecimalMin(value = "0", inclusive = false, message = "Параметр intradayScale должен быть больше 0.")
    Double intradayScale;
}
