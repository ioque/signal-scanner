package ru.ioque.core.dto.scanner.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SectoralFuturesPropertiesDto implements AlgorithmPropertiesDto {
    Double futuresOvernightScale;
    Double stockOvernightScale;
    String futuresTicker;
}
