package ru.ioque.core.dto.scanner.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AnomalyVolumeSignalScannerConfigResponse.class, name = "AnomalyVolumeSignalScannerConfigResponse"),
    @JsonSubTypes.Type(value = CorrelationSectoralScannerConfigResponse.class, name = "CorrelationSectoralScannerConfigResponse"),
    @JsonSubTypes.Type(value = PrefSimpleConfigResponse.class, name = "PrefSimpleConfigResponse"),
    @JsonSubTypes.Type(value = SectoralRetardScannerConfigResponse.class, name = "SectoralRetardScannerConfigResponse") }
)
public abstract class SignalConfigResponse {}
