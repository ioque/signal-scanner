package ru.ioque.core.dto.scanner.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;

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
        Integer workPeriodInMinutes,
        String description,
        List<String> tickers,
        Double historyScale,
        Double intradayScale
    ) {
        super(workPeriodInMinutes, description, tickers);
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
    }
}
