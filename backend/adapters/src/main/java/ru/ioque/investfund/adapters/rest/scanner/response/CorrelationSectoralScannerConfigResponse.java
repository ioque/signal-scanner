package ru.ioque.investfund.adapters.rest.scanner.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.scanner.SectoralFuturesScannerEntity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CorrelationSectoralScannerConfigResponse extends SignalConfigResponse {
    Double futuresOvernightScale;
    Double stockOvernightScale;
    String futuresTicker;

    public static SignalConfigResponse from(SectoralFuturesScannerEntity scanner) {
        return new CorrelationSectoralScannerConfigResponse(
            scanner.getFuturesOvernightScale(),
            scanner.getStockOvernightScale(),
            scanner.getFuturesTicker()
        );
    }
}
