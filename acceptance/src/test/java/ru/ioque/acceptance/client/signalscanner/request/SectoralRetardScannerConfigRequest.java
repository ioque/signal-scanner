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
public class SectoralRetardScannerConfigRequest extends DataScannerConfigRequest {
    Double historyScale;
    Double intradayScale;

    @Builder
    public SectoralRetardScannerConfigRequest(
        List<UUID> ids,
        UUID marketIndexId,
        Double historyScale,
        Double intradayScale
    ) {
        super(ids, marketIndexId);
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
    }
}
