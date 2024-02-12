package ru.ioque.acceptance.adapters.client.signalscanner.request;

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
public class SectoralRetardScannerRequest extends AddSignalScannerRequest {
    Double historyScale;
    Double intradayScale;

    @Builder
    public SectoralRetardScannerRequest(
        String description,
        List<UUID> ids,
        Double historyScale,
        Double intradayScale
    ) {
        super(description, ids);
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
    }
}
