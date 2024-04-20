package ru.ioque.investfund.domain.scanner.algorithms.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
public class AnomalyVolumeProperties implements AlgorithmProperties {
    AlgorithmType type = AlgorithmType.ANOMALY_VOLUME;
    @NotNull(message = "Не передан параметр scaleCoefficient.")
    @DecimalMin(value = "0", inclusive = false, message = "Параметр scaleCoefficient должен быть больше 0.")
    Double scaleCoefficient;
    @NotNull(message = "Не передан параметр historyPeriod.")
    @Min(value = 1, message = "Параметр historyPeriod должен быть больше 0.")
    Integer historyPeriod;
    @NotNull(message = "Не передан идентификатор индекса.")
    @Valid
    Ticker indexTicker;
}
