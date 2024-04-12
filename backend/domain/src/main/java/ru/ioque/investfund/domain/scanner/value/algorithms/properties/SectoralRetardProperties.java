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
public class SectoralRetardProperties implements AlgorithmProperties {
    AlgorithmType type = AlgorithmType.SECTORAL_RETARD;
    @NotNull(message = "Не передан параметр historyScale.")
    @DecimalMin(value = "0", inclusive = false, message = "Параметр historyScale должен быть больше 0.")
    Double historyScale;
    @NotNull(message = "Не передан параметр intradayScale.")
    @DecimalMin(value = "0", inclusive = false, message = "Параметр intradayScale должен быть больше 0.")
    Double intradayScale;
}
