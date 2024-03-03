package ru.ioque.investfund.adapters.rest.signalscanner.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.CorrelationSectoralScannerEntity;

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

    public static SignalConfigResponse from(CorrelationSectoralScannerEntity scanner) {
        return new CorrelationSectoralScannerConfigResponse(
            scanner.getFuturesOvernightScale(),
            scanner.getStockOvernightScale(),
            scanner.getFuturesTicker()
        );
    }
}
