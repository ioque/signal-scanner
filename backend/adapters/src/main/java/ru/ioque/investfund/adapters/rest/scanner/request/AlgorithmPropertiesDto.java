package ru.ioque.investfund.adapters.rest.scanner.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.ioque.investfund.domain.scanner.value.algorithms.properties.AlgorithmProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AnomalyVolumePropertiesDto.class, name = "AnomalyVolumeParameters"),
    @JsonSubTypes.Type(value = PrefCommonPropertiesDto.class, name = "PrefSimpleParameters"),
    @JsonSubTypes.Type(value = SectoralFuturesPropertiesDto.class, name = "SectoralFuturesParameters"),
    @JsonSubTypes.Type(value = SectoralRetardPropertiesDto.class, name = "SectoralRetardParameters") }
)
public interface AlgorithmPropertiesDto {
    AlgorithmProperties toDomain();
}
