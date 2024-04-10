package ru.ioque.investfund.adapters.rest.configurator.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.configurator.command.SavePrefSimpleScanner;
import ru.ioque.investfund.domain.configurator.command.SaveScannerCommand;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrefSimpleScannerRequest extends ScannerRequest {
    @NotNull(message = "The spreadParam is required.")
    Double spreadParam;

    @Builder
    public PrefSimpleScannerRequest(
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<String> tickers,
        Double spreadParam
    ) {
        super(workPeriodInMinutes, description, datasourceId, tickers);
        this.spreadParam = spreadParam;
    }

    @Override
    public SaveScannerCommand toCommand() {
        return SavePrefSimpleScanner.builder()
            .workPeriodInMinutes(getWorkPeriodInMinutes())
            .datasourceId(getDatasourceId())
            .description(getDescription())
            .tickers(getTickers())
            .spreadParam(getSpreadParam())
            .build();
    }
}
