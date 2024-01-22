package ru.ioque.acceptance.client.signalscanner.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CorrelationScannerConfigRequest extends DataScannerConfigRequest {
    Double futuresOvernightScale;
    Double stockOvernightScale;

    @Builder
    public CorrelationScannerConfigRequest(
        List<UUID> ids,
        UUID marketIndexId,
        Double futuresOvernightScale,
        Double stockOvernightScale
    ) {
        super(ids, marketIndexId);
        this.futuresOvernightScale = futuresOvernightScale;
        this.stockOvernightScale = stockOvernightScale;
    }
}
