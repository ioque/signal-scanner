package ru.ioque.investfund.adapters.rest.signalscanner.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.algorithms.CorrelationSectoralSignalConfig;
import ru.ioque.investfund.domain.scanner.entity.SignalConfig;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CorrelationSectoralScannerRequestAdd extends AddSignalScannerRequest {
    Double futuresOvernightScale;
    Double stockOvernightScale;
    String futuresTicker;

    @Builder
    public CorrelationSectoralScannerRequestAdd(
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
