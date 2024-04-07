package ru.ioque.investfund.adapters.rest.configurator.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.configurator.AlgorithmConfig;
import ru.ioque.investfund.domain.configurator.PrefSimpleAlgorithmConfig;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrefSimpleScannerRequest extends ScannerRequest {
    @NotNull(message = "The spreadParam is required.")
    Double spreadParam;

    @Builder
    public PrefSimpleScannerRequest(Integer workPeriodInMinutes, String description, List<String> tickers, Double spreadParam) {
        super(workPeriodInMinutes, description, tickers);
        this.spreadParam = spreadParam;
    }


    @Override
    public AlgorithmConfig buildConfig() {
        return new PrefSimpleAlgorithmConfig(spreadParam);
    }
}
