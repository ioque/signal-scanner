package ru.ioque.investfund.adapters.rest.signalscanner.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.configurator.PrefSimpleScannerConfiguration;
import ru.ioque.investfund.domain.scanner.entity.configurator.ScannerConfiguration;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrefSimpleScannerConfig extends ScannerConfigRequest {
    @NotNull(message = "The spreadParam is required.")
    Double spreadParam;

    @Builder
    public PrefSimpleScannerConfig(Integer workPeriodInMinutes, String description, List<UUID> ids, Double spreadParam) {
        super(workPeriodInMinutes, description, ids);
        this.spreadParam = spreadParam;
    }


    @Override
    public ScannerConfiguration buildConfig() {
        return new PrefSimpleScannerConfiguration(getWorkPeriodInMinutes(), getDescription(), getIds(), spreadParam);
    }
}
