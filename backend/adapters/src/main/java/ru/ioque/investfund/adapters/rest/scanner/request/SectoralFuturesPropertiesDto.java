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
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralFuturesProperties;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SectoralFuturesPropertiesDto implements AlgorithmPropertiesDto {
    @NotNull(message = "Не заполнен параметр futuresOvernightScale.")
    @DecimalMin(value = "0", inclusive = false, message = "Параметр futuresOvernightScale должен быть больше нуля.")
    Double futuresOvernightScale;
    @NotNull(message = "Не заполнен параметр stockOvernightScale.")
    @DecimalMin(value = "0", inclusive = false, message = "Параметр stockOvernightScale должен быть больше нуля.")
    Double stockOvernightScale;
    @NotNull(message = "Не заполнен параметр futuresTicker.")
    String futuresTicker;

    @Override
    public AlgorithmProperties toDomain() {
        return new SectoralFuturesProperties(futuresOvernightScale, stockOvernightScale, futuresTicker);
    }
}
