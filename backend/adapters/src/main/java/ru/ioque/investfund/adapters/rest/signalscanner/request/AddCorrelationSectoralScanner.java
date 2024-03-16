package ru.ioque.investfund.adapters.rest.signalscanner.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.correlationsectoral.CorrelationSectoralSignalConfig;
import ru.ioque.investfund.domain.scanner.entity.SignalConfig;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddCorrelationSectoralScanner extends AddSignalScannerRequest {
    @NotNull(message = "The futuresOvernightScale is required.")
    Double futuresOvernightScale;
    @NotNull(message = "The stockOvernightScale is required.")
    Double stockOvernightScale;
    @NotNull(message = "The futuresTicker is required.")
    String futuresTicker;

    @Builder
    public AddCorrelationSectoralScanner(
        String description,
        List<UUID> ids,
        Double futuresOvernightScale,
        Double stockOvernightScale,
        String futuresTicker
    ) {
        super(description, ids);
        this.futuresOvernightScale = futuresOvernightScale;
        this.stockOvernightScale = stockOvernightScale;
        this.futuresTicker = futuresTicker;
    }

    @Override
    public SignalConfig buildConfig() {
        return new CorrelationSectoralSignalConfig(getIds(), futuresOvernightScale, stockOvernightScale, futuresTicker);
    }
}
