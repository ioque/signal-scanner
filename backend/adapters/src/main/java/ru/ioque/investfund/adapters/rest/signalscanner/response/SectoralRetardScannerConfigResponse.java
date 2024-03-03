package ru.ioque.investfund.adapters.rest.signalscanner.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SectoralRetardScannerEntity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SectoralRetardScannerConfigResponse extends SignalConfigResponse {
    Double historyScale;
    Double intradayScale;

    public static SignalConfigResponse from(SectoralRetardScannerEntity scanner) {
        return new SectoralRetardScannerConfigResponse(scanner.getHistoryScale(), scanner.getIntradayScale());
    }
}
