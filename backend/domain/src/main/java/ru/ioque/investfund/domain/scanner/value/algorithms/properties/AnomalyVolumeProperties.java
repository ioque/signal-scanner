package ru.ioque.investfund.domain.scanner.value.algorithms.properties;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AnomalyVolumeProperties {
    @NotNull(message = "Не передан период работы сканера.")
    @DecimalMin(value = "0", inclusive = false, message = "Параметр scaleCoefficient должен быть больше 0.")
    Double scaleCoefficient;
    @NotBlank(message = "Не передан тикер индекса.")
    String indexTicker;
    @NotNull(message = "Не передан период работы сканера.")
    @Min(value = 1, message = "Период работы сканера должен быть больше 0.")
    Integer historyPeriod;
}
