package ru.ioque.investfund.adapters.rest.scanner.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AlgorithmProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralRetardProperties;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SectoralRetardPropertiesDto implements AlgorithmPropertiesDto {
    @NotNull(message = "Не заполнен параметр historyScale.")
    @DecimalMin(value = "0", inclusive = false, message = "Параметр historyScale должен быть больше нуля.")
    Double historyScale;
    @NotNull(message = "Не заполнен параметр intradayScale.")
    @DecimalMin(value = "0", inclusive = false, message = "Параметр intradayScale должен быть больше нуля.")
    Double intradayScale;

    @Override
    public AlgorithmProperties toDomain() {
        return new SectoralRetardProperties(historyScale, intradayScale);
    }
}
