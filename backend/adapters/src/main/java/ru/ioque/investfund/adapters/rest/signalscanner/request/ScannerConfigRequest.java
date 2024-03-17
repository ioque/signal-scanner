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
import ru.ioque.investfund.domain.scanner.entity.algorithms.AlgorithmConfigurator;

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
    @JsonSubTypes.Type(value = PrefSimpleScannerConfig.class, name = "PrefSimpleRequest"),
    @JsonSubTypes.Type(value = CorrelationSectoralScannerConfig.class, name = "CorrelationSectoralScannerRequest"),
    @JsonSubTypes.Type(value = SectoralRetardScannerConfig.class, name = "SectoralRetardScannerRequest"),
    @JsonSubTypes.Type(value = AnomalyVolumeScannerConfig.class, name = "AnomalyVolumeScannerRequest") }
)
public abstract class ScannerConfigRequest implements Serializable {
    @NotNull(message = "The workPeriodInMinutes is required.")
    Integer workPeriodInMinutes;
    @NotBlank(message = "The description is required.")
    String description;
    @NotNull(message = "The ids is required.")
    List<UUID> ids;
    public abstract AlgorithmConfigurator buildConfig();
}
