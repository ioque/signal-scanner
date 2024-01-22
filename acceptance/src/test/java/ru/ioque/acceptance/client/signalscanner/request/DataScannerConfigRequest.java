package ru.ioque.acceptance.client.signalscanner.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PrefSimpleScannerConfigRequest.class, name = "PrefSimpleScannerConfigRequest"),
    @JsonSubTypes.Type(value = CorrelationScannerConfigRequest.class, name = "CorrelationScannerConfigRequest"),
    @JsonSubTypes.Type(value = SectoralRetardScannerConfigRequest.class, name = "SectoralRetardScannerConfigRequest"),
    @JsonSubTypes.Type(value = AnomalyVolumeScannerConfigRequest.class, name = "AnomalyVolumeScannerConfigRequest") }
)
public class DataScannerConfigRequest implements Serializable {
    List<UUID> ids;
    UUID marketIndexId;
}
