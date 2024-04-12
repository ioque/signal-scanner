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
public class PrefSimpleProperties {
    @DecimalMin(value = "0", inclusive = false, message = "Параметр spreadValue должен быть больше 0.")
    Double spreadValue;
}
