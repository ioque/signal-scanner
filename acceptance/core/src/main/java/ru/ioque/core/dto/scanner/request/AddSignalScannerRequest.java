package ru.ioque.core.dto.scanner.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PrefSimpleRequest.class, name = "PrefSimpleRequest"),
    @JsonSubTypes.Type(value = CorrelationSectoralScannerRequest.class, name = "CorrelationSectoralScannerRequest"),
    @JsonSubTypes.Type(value = SectoralRetardScannerRequest.class, name = "SectoralRetardScannerRequest"),
    @JsonSubTypes.Type(value = AnomalyVolumeScannerRequest.class, name = "AnomalyVolumeScannerRequest") }
)
public abstract class AddSignalScannerRequest implements Serializable {
    Integer workPeriodInMinutes;
    String description;
    UUID datasourceId;
    List<String> tickers;
}
