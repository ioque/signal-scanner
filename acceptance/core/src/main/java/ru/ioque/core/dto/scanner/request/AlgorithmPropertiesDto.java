package ru.ioque.core.dto.scanner.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AnomalyVolumePropertiesDto.class, name = "AnomalyVolumeParameters"),
    @JsonSubTypes.Type(value = PrefSimplePropertiesDto.class, name = "PrefSimpleParameters"),
    @JsonSubTypes.Type(value = SectoralFuturesPropertiesDto.class, name = "SectoralFuturesParameters"),
    @JsonSubTypes.Type(value = SectoralRetardPropertiesDto.class, name = "SectoralRetardParameters") }
)
public interface AlgorithmPropertiesDto {
}
