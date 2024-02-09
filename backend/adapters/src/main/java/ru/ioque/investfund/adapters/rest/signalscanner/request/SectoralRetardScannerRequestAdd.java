package ru.ioque.investfund.adapters.rest.signalscanner.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.financial.algorithms.SectoralRetardSignalConfig;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalConfig;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SectoralRetardScannerRequestAdd extends AddSignalScannerRequest {
    Double historyScale;
    Double intradayScale;

    @Builder
    public SectoralRetardScannerRequestAdd(
        String description,
        List<UUID> ids,
        Double historyScale,
        Double intradayScale
    ) {
        super(description, ids);
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
    }

    @Override
    public SignalConfig buildConfig() {
        return new SectoralRetardSignalConfig(historyScale, intradayScale);
    }
}
