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
import ru.ioque.investfund.domain.scanner.value.algorithms.properties.AlgorithmProperties;
import ru.ioque.investfund.domain.scanner.value.algorithms.properties.PrefSimpleProperties;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrefSimplePropertiesDto implements AlgorithmPropertiesDto {
    @NotNull(message = "Не заполнен параметр spreadValue.")
    @DecimalMin(value = "0", inclusive = false, message = "Параметр spreadValue должен быть больше нуля.")
    Double spreadValue;

    @Override
    public AlgorithmProperties toDomain() {
        return new PrefSimpleProperties(spreadValue);
    }
}
