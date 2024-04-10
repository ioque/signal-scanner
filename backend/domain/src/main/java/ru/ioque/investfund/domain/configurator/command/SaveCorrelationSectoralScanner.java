package ru.ioque.investfund.domain.configurator.command;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.configurator.entity.AlgorithmConfig;
import ru.ioque.investfund.domain.configurator.entity.CorrelationSectoralAlgorithmConfig;

import java.util.List;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SaveCorrelationSectoralScanner extends SaveScannerCommand {
    Double futuresOvernightScale;
    Double stockOvernightScale;
    String futuresTicker;

    @Builder
    public SaveCorrelationSectoralScanner(
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<String> tickers,
        Double futuresOvernightScale,
        Double stockOvernightScale,
        String futuresTicker
    ) {
        super(workPeriodInMinutes, description, datasourceId, tickers);
        this.futuresOvernightScale = futuresOvernightScale;
        this.stockOvernightScale = stockOvernightScale;
        this.futuresTicker = futuresTicker;
    }

    @Override
    public AlgorithmConfig buildConfig() {
        return CorrelationSectoralAlgorithmConfig.builder()
            .futuresOvernightScale(futuresOvernightScale)
            .stockOvernightScale(stockOvernightScale)
            .futuresTicker(futuresTicker)
            .build();
    }
}
