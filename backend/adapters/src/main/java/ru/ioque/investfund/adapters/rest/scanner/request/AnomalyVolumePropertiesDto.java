package ru.ioque.investfund.adapters.rest.scanner.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AlgorithmProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnomalyVolumePropertiesDto implements AlgorithmPropertiesDto {
    @NotNull(message = "Не заполнен параметр scaleCoefficient.")
    @DecimalMin(value = "0", inclusive = false, message = "Параметр scaleCoefficient должен быть больше нуля.")
    Double scaleCoefficient;
    @NotNull(message = "Не заполнен параметр historyPeriod.")
    @DecimalMin(value = "0", inclusive = false, message = "Параметр historyPeriod должен быть больше нуля.")
    Integer historyPeriod;
    @NotBlank(message = "Не заполнен параметр indexTicker.")
    String indexTicker;

    @Override
    public AlgorithmProperties toDomain() {
        return new AnomalyVolumeProperties(scaleCoefficient, historyPeriod, new Ticker(indexTicker));
    }
}
