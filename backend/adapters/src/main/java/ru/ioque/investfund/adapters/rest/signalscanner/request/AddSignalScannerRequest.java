package ru.ioque.investfund.adapters.rest.signalscanner.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @JsonSubTypes.Type(value = AddPrefSimpleScanner.class, name = "PrefSimpleRequest"),
    @JsonSubTypes.Type(value = AddCorrelationSectoralScanner.class, name = "CorrelationSectoralScannerRequest"),
    @JsonSubTypes.Type(value = AddSectoralRetardScanner.class, name = "SectoralRetardScannerRequest"),
    @JsonSubTypes.Type(value = AddAnomalyVolumeScanner.class, name = "AnomalyVolumeScannerRequest") }
)
public abstract class AddSignalScannerRequest implements Serializable {
    @NotBlank(message = "The description is required.")
    String description;
    @NotNull(message = "The ids is required.")
    List<UUID> ids;
    public abstract SignalConfig buildConfig();
}
