package ru.ioque.core.dto.scanner.request;

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
public class PrefSimpleRequest extends AddSignalScannerRequest {
    Double spreadParam;

    @Builder
    public PrefSimpleRequest(
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<String> tickers,
        Double spreadParam
    ) {
        super(workPeriodInMinutes, description, datasourceId, tickers);
        this.spreadParam = spreadParam;
    }
}
