package ru.ioque.investfund.adapters.rest.signalscanner.request;

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
import ru.ioque.investfund.domain.scanner.entity.SignalConfig;

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
    @JsonSubTypes.Type(value = PrefSimpleRequestAdd.class, name = "PrefSimpleRequest"),
    @JsonSubTypes.Type(value = CorrelationSectoralScannerRequestAdd.class, name = "CorrelationSectoralScannerRequest"),
    @JsonSubTypes.Type(value = SectoralRetardScannerRequestAdd.class, name = "SectoralRetardScannerRequest"),
    @JsonSubTypes.Type(value = AnomalyVolumeScannerRequestAdd.class, name = "AnomalyVolumeScannerRequest") }
)
public abstract class AddSignalScannerRequest implements Serializable {
    String description;
    List<UUID> ids;
    public abstract SignalConfig buildConfig();
}
