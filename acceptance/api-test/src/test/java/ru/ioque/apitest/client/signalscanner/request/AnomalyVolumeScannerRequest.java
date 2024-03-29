package ru.ioque.apitest.client.signalscanner.request;

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
public class AnomalyVolumeScannerRequest extends AddSignalScannerRequest {
    Double scaleCoefficient;
    Integer historyPeriod;
    String indexTicker;

    @Builder
    public AnomalyVolumeScannerRequest(
        Integer workPeriodInMinutes,
        String description,
        List<UUID> ids,
        Double scaleCoefficient,
        Integer historyPeriod,
        String indexTicker
    ) {
        super(workPeriodInMinutes, description, ids);
        this.scaleCoefficient = scaleCoefficient;
        this.historyPeriod = historyPeriod;
        this.indexTicker = indexTicker;
    }
}
