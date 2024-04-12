package ru.ioque.investfund.domain.scanner.value.algorithms.properties;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.value.algorithms.AlgorithmType;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PrefSimpleProperties implements AlgorithmProperties {
    AlgorithmType type = AlgorithmType.PREF_SIMPLE;
    @NotNull(message = "Не передан параметр spreadValue.")
    @DecimalMin(value = "0", inclusive = false, message = "Параметр spreadValue должен быть больше 0.")
    Double spreadValue;
}
